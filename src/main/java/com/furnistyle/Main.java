package com.furnistyle;

import com.furnistyle.facade.FurniStyleFacade;
import com.furnistyle.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FurniStyleFacade facade = new FurniStyleFacade();
                MainFrame frame = new MainFrame(facade);
                frame.setVisible(true);
            }
        });
    }
}
