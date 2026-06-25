import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class Interface {
    //private Socket socket;
    private JSlider slider;

    public Interface()//Socket socket) {
    {
        //this.socket = socket;
        JFrame frame = new JFrame();

        frame.setSize(250, 100);
        frame.setBackground(Color.WHITE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));


        this.slider = new JSlider(1, 15, 7);
        this.slider.setMinorTickSpacing(2);
        this.slider.setPaintTicks(true);
        this.slider.addChangeListener(e -> {
            int value = slider.getValue();
            if(value % 2== 0){
                slider.setValue(value + 1);
            }
        });
        JLabel label = new JLabel();
        slider.addChangeListener(e ->{
            label.setText(String.valueOf(slider.getValue()));
        });


        frame.add(slider);
        frame.add(label);

        frame.setVisible(true);



    }

    public JSlider getSlider(){
        return  this.slider;
    }
}
