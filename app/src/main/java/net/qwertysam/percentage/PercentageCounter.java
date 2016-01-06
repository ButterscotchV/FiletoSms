package net.qwertysam.percentage;

import net.dankrushen.filetosms.MainActivity;

public class PercentageCounter {

    private int lastPercentage;
    private int currentPercentage;
    private Tasks task;
    private boolean isStopped;

    public PercentageCounter(Tasks taskName) {
        this.task = taskName;
        lastPercentage = 0;
        currentPercentage = 0;
        isStopped = false;
        //MainActivity.instance().setProcessing(true);
    }

    public Tasks getTask() {
        return task;
    }

    public int getPercentage() {
        return currentPercentage;
    }

    public void updatePercentage(int numerator, int denominator) {
        updatePercentage((long) numerator, (long) denominator);
    }

    public void updatePercentage(long numerator, long denominator) {
        if (!isStopped){
            currentPercentage = PercentageUtil.getPercentage(numerator, denominator);

            if (currentPercentage > lastPercentage) onPercentageChange();
        }
        else{
            System.out.println("ERROR, TRYING TO UPDATE A STOPPED PERCENTAGE COUNTER.");
        }
    }

    private void onPercentageChange() {
        lastPercentage = currentPercentage;

        System.out.println(getTask().title() + ": " + currentPercentage);

        if (currentPercentage == 100){
            stop();
        }
        else{
            //MainActivity.instance().setPercentage(task, currentPercentage);
        }
    }

    public void stop() {
        //MainActivity.instance().setProcessing(false);
        isStopped = true;
    }
}
