package my_project.control;

import KAGO_framework.control.ViewController;
import my_project.view.GUI;

import javax.swing.*;

public class ProgramController {

    private GUI gui;
    private final ViewController viewController;

    public ProgramController(ViewController viewController){
        this.viewController = viewController;
    }

    public void startProgram() {
        Client c = new Client();
        JFrame myFrame = new JFrame("Distort");
        gui = new GUI(c);
        c.setGui(gui);
        myFrame.setBounds(400,200,1000,800);
        myFrame.setContentPane(gui.getPanel());
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(myFrame.EXIT_ON_CLOSE);
    }

    public void send(){
        gui.send();
    }

    public void updateProgram(double dt){

    }
}
