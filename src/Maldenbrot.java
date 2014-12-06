/**
 *
 * @author Shine
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author Shine
 */
public class Maldenbrot extends JFrame{
    /*panel for display mandelbrot set, other set, control panel for mandelbrot, user setting
    *to change the property of main and minor panel, effect setting to change the image effects
    *  custom panel for julia and burning ship 
    */
    JPanel imageScreen, mainScreen, minorScreen, control_panel, user_setting, effect_setting, custom_panel;
    JTextField real_min, real_max, imagin_min, imagin_max, set_iteration, minor_screen_iteration;
    Panel m_panel, other_panel; // panel to display mandelbrot, julia and burning ship
    JTextField point_selected;    // show the user selected point
    Complex user_selected_point;   // the Complex point when user click on mandelbrot set
    JCheckBox antialias_main, antialias_minor, smooth_main, smooth_minor;
    JComboBox choose_formula;   // select julia or burning ship from a combobox
    File image_file;    // file image output

    public Maldenbrot(){
        super("Fractal Application");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Maldenbrot new_window = new Maldenbrot();
                new_window.initialization();
            }
        });
    }

    private void initialization() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // create this panel to display the Complex point whenever user click on mandelbrot
        JPanel selected_point = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel a = new JLabel("User selected point: ");
        point_selected = new JTextField(" ", 40);
        point_selected.setEditable(false);
        selected_point.add(a);
        selected_point.add(point_selected);

        // panel used to display the maldenbrot and julia set
        imageScreen = new JPanel();
        imageScreen.setLayout(new GridLayout(1,2));
        imageScreen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "IMAGE SCREEN", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));

        // panel used to display the maldenbrot
        mainScreen = new JPanel();
        mainScreen.setLayout(new GridLayout(1,1));
        mainScreen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Main Screen", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        m_panel = new Panel(true);
        m_panel.setToolTipText("Drag to zoom in. Left click to draw other fractals");
        m_panel.addMouseListener(new ClickMouse());
        mainScreen.add(m_panel);

        // panel used to display julia set and burning ship
        minorScreen = new JPanel();
        minorScreen.setLayout(new GridLayout(1,1));
        minorScreen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Minor Screen", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        other_panel = new Panel(false);
        other_panel.setToolTipText("Drag to zoom in.");
        minorScreen.add(other_panel);

        // adding two panel to main panel
        imageScreen.add(mainScreen);
        imageScreen.add(minorScreen);

        // panel for user's custom
        control_panel = new JPanel();
        control_panel.setLayout(new BorderLayout());
        control_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "CONTROL PANEL", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));

        // changing the iteration, axis of maldenbrot set
        user_setting = new JPanel();
        user_setting.setLayout(new GridLayout(3, 1));
        user_setting.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "User Setting", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        // generate custom elements for user_setting panel
        JPanel real_setting = new JPanel();
        JPanel imagin_panel = new JPanel();
        JPanel iteration = new JPanel();
        JLabel real = new JLabel("Real: ");
        real_min = new JTextField("-2.0", 5);
        JLabel to_1 = new JLabel("to");
        real_max = new JTextField("2.0", 5);
        real_setting.add(real);
        real_setting.add(real_min);
        real_setting.add(to_1);
        real_setting.add(real_max);
        JLabel imagin = new JLabel ("Imaginary: ");
        imagin_min = new JTextField("-1.6", 5);
        JLabel to_2 = new JLabel("to");
        imagin_max = new JTextField("1.6", 5);
        imagin_panel.add(imagin);
        imagin_panel.add(imagin_min);
        imagin_panel.add(to_2);
        imagin_panel.add(imagin_max);
        JLabel iter = new JLabel("Iteration: ");
        set_iteration = new JTextField("100", 5);
        JButton draw_main_panel = new JButton("Draw");
        draw_main_panel.setToolTipText("Click to draw Mandelbrot");
        draw_main_panel.addActionListener(new DrawListenerHandler());
        iteration.add(iter);
        iteration.add(set_iteration);
        iteration.add(draw_main_panel);
        user_setting.add(real_setting);
        user_setting.add(imagin_panel);
        user_setting.add(iteration);

        // changing smooth, zoom in, zoom out, reset the image
        effect_setting = new JPanel();
        effect_setting.setLayout(new GridLayout(1, 2));
        effect_setting.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Image Effects", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        // generate custom elements for main_screen_setting panel  
        JPanel main_screen_setting = new JPanel();
        main_screen_setting.setLayout(new GridLayout(3,1));
        main_screen_setting.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Main screen options", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        JPanel zoom_main = new JPanel();
        JPanel smooth_antilias_main = new JPanel();
        JPanel main_panel_status = new JPanel();
        JButton zoom_in_main = new JButton("Zoom in");
        zoom_in_main.addActionListener(new MainZoomInListener());
        JButton zoom_out_main = new JButton("Zoom out");
        zoom_out_main.addActionListener(new MainZoomOutListener());
        JButton reset_main = new JButton("Reset");
        reset_main.addActionListener(new ResetMainListener());
        zoom_main.add(zoom_in_main);
        zoom_main.add(zoom_out_main);
        zoom_main.add(reset_main);
        smooth_main = new JCheckBox("Smooth");
        smooth_main.addActionListener(new SmoothMainListener());
        antialias_main = new JCheckBox("Antilias");
        antialias_main.addActionListener(new AntialiasActionListener());
        smooth_antilias_main.add(smooth_main);
        smooth_antilias_main.add(antialias_main);
        JLabel main_status = new JLabel("Status: ");
        main_panel_status.add(main_status);
        main_panel_status.add(m_panel.getLabel());

        main_screen_setting.add(zoom_main);
        main_screen_setting.add(smooth_antilias_main);
        main_screen_setting.add(main_panel_status);

        // generate custom elements for minor_screen_setting panel  
        JPanel minor_screen_setting = new JPanel();
        minor_screen_setting.setLayout(new GridLayout(3,1));
        minor_screen_setting.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Minor screen options", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        JPanel zoom_minor = new JPanel();
        JPanel smooth_antilias_minor = new JPanel();
        JPanel minor_panel_status = new JPanel();
        JButton zoom_in_minor = new JButton("Zoom in");
        zoom_in_minor.addActionListener(new MinorZoomInListener());
        JButton zoom_out_minor = new JButton("Zoom out");
        zoom_out_minor.addActionListener(new MinorZoomOutListener());
        JButton reset_minor = new JButton("Reset");
        reset_minor.addActionListener(new ResetMinorListener());
        zoom_minor.add(zoom_in_minor);
        zoom_minor.add(zoom_out_minor);
        zoom_minor.add(reset_minor);
        smooth_minor = new JCheckBox("Smooth");
        smooth_minor.addActionListener(new SmoothMinorListener());
        antialias_minor = new JCheckBox("Antilias");
        JLabel minor_status = new JLabel("Status: ");
        minor_panel_status.add(minor_status);
        minor_panel_status.add(other_panel.getLabel());
        smooth_antilias_minor.add(smooth_minor);
        smooth_antilias_minor.add(antialias_minor);
        minor_screen_setting.add(zoom_minor);
        minor_screen_setting.add(smooth_antilias_minor);
        minor_screen_setting.add(minor_panel_status);

        effect_setting.add(main_screen_setting);
        effect_setting.add(minor_screen_setting);

        // export image, let user able to choose which set goin to be displayed on minor panel
        custom_panel = new JPanel();
        custom_panel.setLayout(new GridLayout(4,1));
        custom_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Custom image", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
        //generate custom elements for custom_panel
        JPanel form = new JPanel();
        JPanel save_main_screen = new JPanel();
        JPanel save_minor_screen = new JPanel();
        JPanel draw_button = new JPanel();
        JLabel formula = new JLabel("Formulas: ");
        String[] some_formula = {"Julia", "Burning Ship"};
        choose_formula = new JComboBox(some_formula);
        choose_formula.addItemListener(new ComboBoxListener());

        JLabel optional_iteration = new JLabel("Iteration: ");
        minor_screen_iteration = new JTextField("100", 5);
        form.add(formula);
        form.add(choose_formula);
        form.add(optional_iteration);
        form.add(minor_screen_iteration);
        JButton save_main_image = new JButton("Save Main Image");
        save_main_image.addActionListener(new SaveMainImageListener());
        save_main_image.setIcon(new ImageIcon("C:\\Users\\Shine\\Documents\\NetBeansProjects\\Maldenbrot\\src\\maldenbrot\\saving.gif"));
        save_main_screen.add(save_main_image);
        JButton save_minor_image = new JButton("Save Minor Image");
        save_minor_image.setIcon(new ImageIcon("C:\\Users\\Shine\\Documents\\NetBeansProjects\\Maldenbrot\\src\\maldenbrot\\saving.gif"));
        save_minor_image.addActionListener(new SaveMinorImageListener());
        save_minor_screen.add(save_minor_image);
        JButton draw_minor_panel = new JButton("Draw");
        draw_minor_panel.setToolTipText("Click to draw formula");
        draw_minor_panel.addActionListener(new DrawOtherFormulaListener());
        draw_button.add(draw_minor_panel);
        custom_panel.add(form);
        custom_panel.add(draw_button);
        custom_panel.add(save_main_screen);
        custom_panel.add(save_minor_screen);

        // adding components to control panel
        control_panel.add(user_setting, BorderLayout.WEST);
        control_panel.add(effect_setting, BorderLayout.CENTER);
        control_panel.add(custom_panel, BorderLayout.EAST);

        // adding to main panel to frame   
        this.add(selected_point, BorderLayout.NORTH);
        this.add(imageScreen, BorderLayout.CENTER);
        this.add(control_panel, BorderLayout.SOUTH);

        pack();
        this.setVisible(true);
    }

    public void saveImageMain(){    // save main button calls this method
        JFileChooser save_file = new JFileChooser();
        save_file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        save_file.setFileFilter(new JPEGFileFilter());
        save_file.setAcceptAllFileFilterUsed(false);
        int returnValue = save_file.showSaveDialog(this);
        String ext = "";
        String extension = "";

        if(returnValue == JFileChooser.APPROVE_OPTION){
            image_file = save_file.getSelectedFile();
            extension = save_file.getFileFilter().getDescription();
            if(extension.equals("JPEG file images *.jpeg,*.JPEG")){
                ext = "JPEG";
                image_file = new File(image_file.getAbsolutePath() + ".jpeg");
            }
            try{
                if(image_file != null){
                    m_panel.drawToSave(m_panel.getWidth(), m_panel.getHeight());
                    ImageIO.write(m_panel.getImageToSave(), ext, image_file);
                }

            }catch (IOException e){

            }
        }
    }

    public void saveImageMinor(){    // save minor button calls this method
        JFileChooser save_file = new JFileChooser();
        save_file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        save_file.setFileFilter(new JPEGFileFilter());
        save_file.setFileFilter(new gifSaveFilter());
        save_file.setAcceptAllFileFilterUsed(true);
        int returnValue = save_file.showSaveDialog(this);
        String ext = "";
        String extension = "";

        if(returnValue == JFileChooser.APPROVE_OPTION){
            image_file = save_file.getSelectedFile();
            extension = save_file.getFileFilter().getDescription();
            if(extension.equals("JPEG file images *.jpeg,*.JPEG")){
                ext = "JPEG";
                image_file = new File(image_file.getAbsolutePath() + ".jpeg");
            }
            try{
                if(image_file != null){
                    other_panel.drawToSave(m_panel.getWidth(), other_panel.getHeight());
                    ImageIO.write(other_panel.getImageToSave(), ext, image_file);
                }

            }catch (IOException e){

            }
        }
    }

    class DrawListenerHandler implements ActionListener{    // draw the main image by specified setting when click on draw button

        @Override
        public void actionPerformed(ActionEvent e) {
            double r_max = Double.parseDouble(real_max.getText());
            double r_min = Double.parseDouble(real_min.getText());
            double i_max = Double.parseDouble(imagin_max.getText());
            double i_min = Double.parseDouble(imagin_min.getText());
            int itr = Integer.parseInt(set_iteration.getText());
            m_panel.setElement(itr, r_max, r_min, i_max, i_min);
            m_panel.start();
        }
    }

    class DrawOtherFormulaListener implements ActionListener {  // draw the minor image by its iteration when click on draw button

        @Override
        public void actionPerformed(ActionEvent e) {
            other_panel.setIteration(Integer.parseInt(minor_screen_iteration.getText()));
            other_panel.start();
        }
    }

    class ClickMouse implements MouseListener{  // display comlex point and draw julia set

        @Override
        public void mouseClicked(MouseEvent e) {
            if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0){
                String s;
                Double dx = (Double.parseDouble(real_max.getText())- Double.parseDouble(real_min.getText()))*e.getX()/m_panel.getWidth() + Double.parseDouble(real_min.getText());
                Double dy = (Double.parseDouble(imagin_max.getText()) - Double.parseDouble(imagin_min.getText()))* e.getY()/ m_panel.getHeight() + Double.parseDouble(imagin_min.getText());
                user_selected_point = new Complex(dx, dy);
                if(dy > 0){
                    s = dx.toString() + "+" + dy.toString() + "i";
                }else {
                    s = dx.toString() + dy.toString() + "i";
                }
                point_selected.setText(s);
                if(other_panel.getOtherFormularMode() == 0){
                    other_panel.setPointJulia(user_selected_point);
                    other_panel.start();
                }
            }
        }
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

    }

    class AntialiasActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            m_panel.setAntilias();
            m_panel.start();
        }
    }

    class SmoothMainListener implements ActionListener{     // set smooth main panel
        @Override
        public void actionPerformed(ActionEvent e) {
            m_panel.setSmooth();
            m_panel.start();
        }
    }

    class SmoothMinorListener implements ActionListener{      // set smooth minor panel
        @Override
        public void actionPerformed(ActionEvent e) {
            other_panel.setSmooth();
            other_panel.start();
        }

    }

    class MainZoomInListener implements ActionListener{     // zoom in the mandelbrot set

        @Override
        public void actionPerformed(ActionEvent e) {
            double zoom = m_panel.getZoom();
            double viewX = m_panel.getViewX();
            double viewY = m_panel.getViewY();
            viewX += 0.025 * zoom;
            viewY += 0.025 * zoom;
            zoom /= 1.25;
            m_panel.setZoomProperty(viewX, viewY, zoom);
            m_panel.start();
        }

    }

    class MainZoomOutListener implements ActionListener{    // zoom in the mandelbrot set

        @Override
        public void actionPerformed(ActionEvent e) {
            double zoom = m_panel.getZoom();
            double viewX = m_panel.getViewX();
            double viewY = m_panel.getViewY();
            viewX += 0.025 * zoom;
            viewY += 0.025 * zoom;
            zoom *= 1.25;
            m_panel.setZoomProperty(viewX, viewY, zoom);
            m_panel.start();
        }
    }


    class MinorZoomInListener implements ActionListener{    // zoom in the julia set

        @Override
        public void actionPerformed(ActionEvent e) {
            double zoom = other_panel.getZoom();
            double viewX = other_panel.getViewX();
            double viewY = other_panel.getViewY();
            viewX += 0.025 * zoom;
            viewY += 0.025 * zoom;
            zoom /= 1.25;
            other_panel.setZoomProperty(viewX, viewY, zoom);
            other_panel.start();
        }
    }

    class MinorZoomOutListener implements ActionListener{   // zoom out the julia set

        @Override
        public void actionPerformed(ActionEvent e) {
            double zoom = other_panel.getZoom();
            double viewX = other_panel.getViewX();
            double viewY = other_panel.getViewY();
            viewX += 0.025 * zoom;
            viewY += 0.025 * zoom;
            zoom *= 1.25;
            other_panel.setZoomProperty(viewX, viewY, zoom);
            other_panel.start();
        }
    }

    class ResetMainListener implements ActionListener{  // reset to original image

        @Override
        public void actionPerformed(ActionEvent e) {
            m_panel.setZoomProperty(0.0, 0.0, 1);
            m_panel.start();
        }
    }

    class ResetMinorListener implements ActionListener {    // reset to original image

        @Override
        public void actionPerformed(ActionEvent e) {
            other_panel.setZoomProperty(0.0, 0.0, 1);
            other_panel.start();
        }
    }

    class ComboBoxListener implements ItemListener {    // a drop down box handler

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED){
                switch(choose_formula.getSelectedIndex()){
                    case 0:
                        other_panel.setOtherFormula(0);
                        smooth_minor.setEnabled(true);
                        break;
                    case 1:
                        other_panel.setOtherFormula(1);
                        smooth_minor.setEnabled(false);
                        break;
                }
                other_panel.setZoomProperty(0.0, 0.0, 1.0);
                other_panel.start();
            }
        }
    }

    class SaveMinorImageListener implements ActionListener {    // listener for save minor image button

        @Override
        public void actionPerformed(ActionEvent e) {
            saveImageMinor();
        }

    }

    class SaveMainImageListener implements ActionListener { // listener for save main image button

        @Override
        public void actionPerformed(ActionEvent e) {
            saveImageMain();
        }

    }
}

// class used to set extension of file to jpeg 
class JPEGFileFilter extends javax.swing.filechooser.FileFilter{
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String s = f.getName();
        return s.endsWith(".jpeg") || s.endsWith(".JPEG");
    }
    @Override
    public String getDescription() {
        return "JPEG file images *.jpeg,*.JPEG";
    }
}

/**
 * Class gifSaveFilter
 * Provide filter for saving image in JGIF format
 */
class gifSaveFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String s = f.getName();
        return s.endsWith(".gif") || s.endsWith(".GIF");
    }
    public String getDescription() {
        return "GIF file images *.gif,*.GIF";
    }
}

class Complex {
    double real, imaginary;

    public Complex(){
        this.real = 0;
        this.imaginary = 0;
    }

    public Complex(double real, double imaginary){
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal(){
        return real;
    }

    public double getImaginary(){
        return imaginary;
    }

    public Complex square(){
        double temp_real, temp_imagin;
        temp_real = real*real - imaginary*imaginary;
        temp_imagin = 2*real*imaginary;
        real = temp_real;
        imaginary = temp_imagin;
        return new Complex(real, imaginary);

    }

    public Complex add(Complex d){
        return new Complex((d.getReal() + this.getReal()), (d.getImaginary() + this.getImaginary()));
    }

    public double modulusSquared(){
        return real*real + imaginary*imaginary;
    }
}

class Panel extends JPanel implements Runnable, MouseListener, MouseMotionListener{
    BufferedImage image, imageToSave;   // create 2 bufferediamage to draw on panel and other one for saving image
    Complex point;    // complex point to draw Julia
    JLabel status;  // status of each panel. Rendering image or done rendering
    boolean mandelbrot_mode;    // draw mandelbrot if true, draw julia and burning ship if false
    int otherFormulaeMode = 0; // mode 0 is Julia, 1 is burning ship
    int number_of_iteration;
    int width, height;    // current width and height of the drawing panel    
    double real_portion_max, real_portion_min, imagin_portion_max, imagin_portion_min;   // portion for real and imaginary of complex number
    boolean antialias = false, smooth = false;  // set antialias and smooth for picture
    double zoom = 1, viewX = 0.0, viewY = 0.0;  // zoom elements
    boolean dragging = false;    // if dragging or not
    private int mouseX, mouseY; // mouse position when the button was pressed
    private int dragX, dragY; // current mouse position during dragging
    Graphics2D graphics, graphicToSave;   // off screen to draw image and save image
    Thread thread;  // rendering thread

    public Panel(boolean mode){
        this.setPreferredSize(new Dimension(500, 400)); // default size
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.mandelbrot_mode = mode;    // set mode 
        this.real_portion_min = -2;
        this.real_portion_max = 2;
        this.imagin_portion_min = -1.6;
        this.imagin_portion_max = 1.6;
        this.number_of_iteration = 100;
        this.point = new Complex(0.0, 0.0);     // initial point to draw Julia
        this.setDoubleBuffered(true);
        this.thread = null;
        this.status = new JLabel();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // running this thread
    @Override
    public void run() {
        while (thread != null) {
            while (draw());     // draw image
            synchronized (this){
                try {
                    wait();     // wait until finish drawing to show on screen
                }catch (InterruptedException exp){
                }
                notifyAll();
            }
        }
    }

    // starting this thread
    public void start(){
        redraw();
    }

    private void redraw() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        } else {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    // draw off screen
    public boolean draw(){
        Dimension size = this.getSize();
        // create off screen buffer for double buffer 
        if(image == null || width != size.width || height != size.height){
            width = size.width;
            height = size.height;
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics = (Graphics2D) image.getGraphics();
        }
        //draw this off screen
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                double r = zoom / Math.min(width, height);
                double dx = (real_portion_max - real_portion_min) * ( x * r + viewX) + real_portion_min;
                double dy = (imagin_portion_max - imagin_portion_min) * (y * r + viewY) + imagin_portion_min;
                Color color = color(dx, dy);
                if(antialias){  // if antialias == true
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
                graphics.setColor(color);
                graphics.fillRect(x, y, 1, 1);
                status.setText("Rendering....");
            }
        }
        repaint();  // just repaint
        status.setText("Done.");
        return false;   // finish drawing
    }

    // paint method to draw off screen image and draw a rectangular for zooming
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Dimension size = this.getSize();
        if(width != size.width || height != size.height){
            redraw();
        }
        g.drawImage(image, 0, 0, null);
        if (dragging){      // draw rectangular for dragging
            g.setColor(Color.red);
            int x = Math.min(mouseX, dragX);
            int y = Math.min(mouseY, dragY);
            double w = mouseX + dragX - 2 * x;
            double h = mouseY + dragY - 2 * y;
            double r = Math.max(w/ width, h/height);
            g.drawRect(x, y, (int) (width * r), (int) (height * r));
        }
    }

    // this method is provided to save image with its current size
    public void drawToSave(int width, int height){
        imageToSave = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphicToSave = (Graphics2D) imageToSave.getGraphics();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                double r = zoom / Math.min(width, height);
                double dx = (real_portion_max - real_portion_min) * ( x * r + viewX) + real_portion_min;
                double dy = (imagin_portion_max - imagin_portion_min) * (y * r + viewY) + imagin_portion_min;
                Color color = color(dx, dy);
                if(antialias){
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
                graphicToSave.setColor(color);
                graphicToSave.fillRect(x, y, 1, 1);
            }
        }
    }

    // provide color for each pixel 
    public Color color(double x, double y){
        double color_index;
        if(mandelbrot_mode){       // provide color for mandelbrot set
            color_index = computeMaldenbrot(new Complex(0.0, 0.0), new Complex(x, y));
            float h = (float) (color_index/number_of_iteration);
            float b = 1.0f -h*h;
            return Color.getHSBColor((float) (0.1+h), 0.95f, b);
        }else{      // provide color for other set
            color_index = computeOtherFractal(new Complex(x, y), point);
            float h = (float) (color_index/number_of_iteration);
            float b = 1.0f -h*h;
            return Color.getHSBColor((float) (0.1+h), 0.95f, b);
        }
    }

    // compute mandelbrot set
    public double computeMaldenbrot(Complex number, Complex point){
        int iteration = 0;
        while (iteration < number_of_iteration && number.modulusSquared() < 4){
            number = number.square().add(point);
            iteration++;
        }
        if(!smooth){    // not smooth
            return iteration;
        }else{  // smooth
            number = number.square().add(point);
            number = number.square().add(point);
            iteration++;
            double nsmooth = iteration + 1 - (Math.log(Math.log(Math.sqrt(number.modulusSquared()))))/Math.log(2);
            return nsmooth;
        }
    }


    // return a buffered image to save 
    public BufferedImage getImageToSave(){
        return imageToSave;
    }

    // set elements: real and imagin portion, number of iteration
    public void setElement(int number_of_iteration, double real_portion_max, double real_portion_min, double imagin_portion_max, double imagin_portion_min){
        this.real_portion_max = real_portion_max;
        this.real_portion_min = real_portion_min;
        this.imagin_portion_max = imagin_portion_max;
        this.imagin_portion_min = imagin_portion_min;
        this.number_of_iteration = number_of_iteration;
    }

    //compute other fractal
    public double computeOtherFractal(Complex number, Complex point){
        int iter = 0;
        double julia_smooth = Math.exp(-Math.sqrt(number.modulusSquared()));
        if(otherFormulaeMode == 0){     // compute julia 
            while(iter < number_of_iteration && number.modulusSquared() < 4){
                number = number.square().add(point);
                iter++;
                julia_smooth += Math.exp(-Math.sqrt(number.modulusSquared()));
            }
            if (!smooth){
                return iter;
            }else{
                return julia_smooth;
            }
        }else{  // compute burning ship
            if(otherFormulaeMode == 1){
                Complex z = new Complex(0.0, 0.0);
                while (iter < number_of_iteration && z.modulusSquared() < 4.0) {
                    z = new Complex(Math.abs(z.getReal()),Math.abs(z.getImaginary())).square().add(number);
                    iter++;
                }
            }
            return iter;
        }
    }

    // set point for julia
    public void setPointJulia(Complex point){
        this.point = point;
    }

    //set number of iteration
    public void setIteration(int a){
        this.number_of_iteration = a;
    }

    // get index of formula in list of formula in custom image panel
    public int getOtherFormularMode(){
        return otherFormulaeMode;
    }

    // set antialias for image
    public void setAntilias(){
        this.antialias = !this.antialias;
    }

    // set smooth for image
    public void setSmooth(){
        this.smooth = !this.smooth;
    }

    // get status in drawing image
    public JLabel getLabel(){
        return status;
    }

    // get relational coordinate of X after zooming
    public double getViewX(){
        return viewX;
    }

    // get relational coordinate of Y after zooming
    public double getViewY(){
        return viewY;
    }

    // the fraction between relational coordinate and actual coordinate
    public double getZoom(){
        return zoom;
    }

    // set zoom elements
    public void setZoomProperty(double viewX, double viewY, double zoom){
        this.viewX = viewX;
        this.viewY = viewY;
        this.zoom = zoom;
    }

    // set mode of formula
    public void setOtherFormula(int otherFormulaeMode){
        this.otherFormulaeMode = otherFormulaeMode;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = dragX = e.getX();  // save the position of mouse when pressing
        mouseY = dragY = e.getY();
        dragging = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        int x = e.getX();   // save the coordinate (x,y) when mouse released
        int y = e.getY();

        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {//if left mouse button
            double r = zoom/Math.min(width, height);
            if (x != mouseX && y != mouseY) {
                int mx = Math.min(x, mouseX);
                int my = Math.min(y, mouseY);
                viewX += mx * r;
                viewY += my * r;
                double w = x + mouseX - 2 * mx;
                double h = y + mouseY - 2 * my;
                zoom *= Math.max(w / width, h / height);//set zoom
                redraw();//recompute anything and repaint
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {//Dragged by left mouse button
            dragX = e.getX();
            dragY = e.getY();
            repaint();
        }
    }

    //not used
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}

