package net.qwertysam.percentage;


public enum Tasks {

    IDLE("Waiting...", 0, 0),

    LOADING_BYTES("Loading Bytes", 1, 3),
    CONV_BASE64("Compressing", 2, 3),
    SEND_STRINGS("Sending", 3, 3),

    LOAD_MESSAGES("Parsing Messages", 1, 3),
    CONV_BACK_BASE64("Decompressing", 2, 3),
    WRITING_BYTES("Writing Bytes", 3, 3);

    Tasks(String key, int operationNumber, int maxOpers){
        this.key = key;
        this.operationNumber = operationNumber;
        this.maxOpers = maxOpers;
    }

    private String key;
    private int operationNumber;
    private int maxOpers;

    public String title(){
        return key;
    }

    public int getTaskNum(){
        return operationNumber;
    }

    public int getTotalTaskNum(){
        return maxOpers;
    }
}
