package net.dankrushen.filetosms;

import android.app.Dialog;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.qwertysam.codec.ByteUtil;
import net.qwertysam.percentage.Tasks;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    public static MainActivity instance(){
        return instance;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Button buttonOpenDialog;
        String sell = "null";
        Button senddata;
        TextView percentage;
        ProgressBar progressBar;
        TextView taskDisplay;
        EditText phoneNum;
        boolean isRunning = false;

        //File chooser dialog
        ListView dialog_ListView;
        File root;
        File curFolder;
        List<String> fileList = new ArrayList<>();
        Button buttonUp;
        TextView textFolder;
        TextView fileSelected;

        View rootView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNum = getArguments().getInt(ARG_SECTION_NUMBER);
            rootView = (sectionNum == 1 ? inflater.inflate(R.layout.sendfragment, container, false) : inflater.inflate(R.layout.receivefragment, container, false));

            if(sectionNum == 1) {
                buttonOpenDialog = (Button) rootView.findViewById(R.id.opendialog);
                buttonOpenDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileChooser();
                    }
                });

                root = new File("/storage/");
                curFolder = root;
                progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                percentage = (TextView) rootView.findViewById(R.id.percentage);
                taskDisplay = (TextView) rootView.findViewById(R.id.curProc);
                phoneNum = (EditText) rootView.findViewById(R.id.phoneNum);

                setPercentage(Tasks.IDLE, 0);
                setProcessing(false);

                senddata = (Button) rootView.findViewById(R.id.send);
                senddata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isRunning) {
                            if (!sell.equals("null")) {
                                if (!phoneNum.getText().toString().replaceAll("\\D+", "").isEmpty() && phoneNum.getText() != null) {
                                    warningDialog();
                                } else {
                                    Toast.makeText(getActivity(), "Please enter a phone number!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Please select a file!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please wait for the current process to finish!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            return rootView;
        }

        private void fileChooser(){
            // custom dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialoglayout);
            dialog.setCancelable(true);
            dialog.setTitle("File Chooser");

            textFolder = (TextView) dialog.findViewById(R.id.folder);
            buttonUp = (Button) dialog.findViewById(R.id.up);
            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListDir(curFolder.getParentFile());
                }
            });

            dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);

            ListDir(curFolder);

            dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    File selected = new File(fileList.get(position));
                    if (selected.isDirectory()) {
                        ListDir(selected);
                    } else {
                        fileSelected = (TextView) rootView.findViewById(R.id.fileSelected);
                        fileSelected.setText(selected.getName());
                        sell = selected.toString();
                        Toast.makeText(getActivity(), selected.toString() + " selected",
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }

        private void ListDir(File f) {
            if (f.equals(root)) {
                buttonUp.setEnabled(false);
            } else {
                buttonUp.setEnabled(true);
            }

            curFolder = f;
            textFolder.setText(f.getPath());

            File[] files = f.listFiles();
            fileList.clear();

            for (File file : files) {
                if(file.length() != 0) {
                    fileList.add(file.getPath());
                }
            }

            ArrayAdapter<String> directoryList = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, fileList);
            dialog_ListView.setAdapter(directoryList);
        }

        public void setPercentage(final Tasks task, final int percent) {
            this.getActivity().runOnUiThread(new Thread() {
                public void run() {
                    percentage.setText(percent + "%");
                    progressBar.setProgress(percent);
                    taskDisplay.setText(task.title() + " (" + task.getTaskNum() + "/" + task.getTotalTaskNum() + ")");
                }
            });
        }

        public void setProcessing(final boolean visible){
            this.getActivity().runOnUiThread(new Thread() {
                public void run() {
                    percentage.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    taskDisplay.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                    progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }

        public String getTime(long seconds){
            int days = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (days *24);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
            return (days == 0 ? "" : days + "d ") + (hours == 0 ? "" : hours + "h ") + (minute == 0 ? "" : minute + "m ") + (second == 0 ? "" : second + "s");
        }

        public String[] splitStringEvery(String s, int interval) {
            int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
            String[] result = new String[arrayLength];

            int j = 0;
            int lastIndex = result.length - 1;
            for (int i = 0; i < lastIndex; i++) {
                result[i] = s.substring(j, j + interval);
                j += interval;
            } //Add the last bit
            result[lastIndex] = s.substring(j);

            return result;
        }

        private void warningDialog(){
            // custom dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialogwarn);
            dialog.setCancelable(true);
            dialog.setTitle("Estimates");
            TextView warnTxtFile = (TextView) dialog.findViewById(R.id.warnTxtFile);
            warnTxtFile.setText(fileSelected.getText());
            TextView warnTxtBytes = (TextView) dialog.findViewById(R.id.warnTxtBytes);
            File sellect = new File(sell);
            warnTxtBytes.setText(String.valueOf(sellect.length()) + " bytes");
            TextView warnTxtMessages = (TextView) dialog.findViewById(R.id.warnTxtMessages);
            double massages = Math.ceil(sellect.length()/105)+3;
            double messagesNum = (massages == 3 ? massages + 1 : massages);
            warnTxtMessages.setText(String.format("%.0f", messagesNum).replace(".0", "") + " messages");
            double processTime = Math.ceil((sellect.length() * 0.0000006353240152) + 1)-1;
            double fullTime = processTime + messagesNum;
            TextView warnTxtEstTime = (TextView) dialog.findViewById(R.id.warnTxtEstTime);
            warnTxtEstTime.setText(getTime((long) fullTime));
            Button cnclBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            // if button is clicked, close the custom dialog
            cnclBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button contBtn = (Button) dialog.findViewById(R.id.continueBtn);
            // if button is clicked, close the custom dialog
            contBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Converting file...", Toast.LENGTH_SHORT).show();
                    isRunning = true;
                    byteprocessor();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

        private void byteprocessor() {
            setProcessing(true);

            new Thread() {
                public void run() {

                    List<String> encoded = ByteUtil.splitIntoSendables(ByteUtil.encodeBytes(ByteUtil.bytesFromFile(sell)));

                    SmsManager sms = SmsManager.getDefault();
                    String phoneNumber = phoneNum.getText().toString().replaceAll("\\D+", "");

                    System.out.println("Sending Messages: " + encoded.size());

                    File sellect = new File(sell);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    System.out.println( sdf.format(cal.getTime()) );

                    String firstMessage = "<(<(||\"" + fileSelected.getText() + "\"," + sellect.length() + " bytes," + encoded.size() + " message(s)," + sdf.format(cal.getTime()) + "||";

                    for(String sendable : splitStringEvery(firstMessage, 140)) {
                        sms.sendTextMessage(phoneNumber, null, sendable, null, null);
                    }

                    for (String toSend : encoded) {
                        System.out.println("Sending Message: " + toSend);
                        sms.sendTextMessage(phoneNumber, null, toSend, null, null);
                    }

                    sms.sendTextMessage(phoneNumber, null, "||)>)>", null, null);

                    isRunning = false;
                }
            }.start();

            setProcessing(false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Send";
                case 1:
                    return "Receive";
            }
            return null;
        }
    }
}
