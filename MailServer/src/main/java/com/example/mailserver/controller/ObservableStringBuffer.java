package com.example.mailserver.controller;

import javafx.beans.binding.StringBinding;

public class ObservableStringBuffer extends StringBinding {
    private final StringBuffer buffer = new StringBuffer();
    private int nLines = 0;
    private final int MAX_LINES = 50;

    @Override
    protected String computeValue() {
        return buffer.toString();
    }

    public void reset() {
        buffer.replace(0, buffer.length(), "");
        nLines = 0;
        invalidate();
    }

    public void addLine(String text) {
        String lineSeparator = "\n";
        if(buffer.length() == 0){
            lineSeparator = "";
        }
        buffer.append(lineSeparator + text);
        nLines++;
        controlMaxLines();
        invalidate();
    }

    private void controlMaxLines(){
        if(nLines > MAX_LINES){
            buffer.replace(0, buffer.indexOf("\n") + 1, "");
            nLines--;
        }
    }
}
