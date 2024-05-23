import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Random;

public class CookieClicker extends JFrame {
    private JTextField counterField;
    private int randomClicks;
    private int currentClicks;
    private int totalClicks;
    private ImageIcon originalIcon;
    private ImageIcon enlargedIcon;
    private Random random;

    public CookieClicker(){
        super("Cookie Clicker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 800));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);

        random = new Random();
        resetRandomClicks();

        addGuiComponents();
    }

    private void resetRandomClicks() {
        randomClicks = random.nextInt(50) + 1;
        currentClicks = 0;
    }

    private void playSound(String soundFile) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(soundFile);
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Ошибка при воспроизведении звука: " + e);
        }
    }
    private void addGuiComponents(){
        SpringLayout springLayout = new SpringLayout();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(springLayout);

        //1. Banner
        JLabel bannerImg = loadImage("resources/banner.png", true, 450, 220);

        jPanel.add(bannerImg);
        springLayout.putConstraint(SpringLayout.WEST, bannerImg, 65, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.NORTH, bannerImg, 25, SpringLayout.NORTH, jPanel);

        //2. Cookie Button
        originalIcon = new ImageIcon(getClass().getResource("resources/cow.png"));
        enlargedIcon = new ImageIcon(resizeImage(((ImageIcon) originalIcon).getImage(),
                originalIcon.getIconWidth() + 10, originalIcon.getIconHeight() + 10));

        JButton cookieButton = new JButton(originalIcon);
        cookieButton.setBorder(BorderFactory.createEmptyBorder());
        cookieButton.setContentAreaFilled(false);
        cookieButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int counter = Integer.parseInt(counterField.getText());
                totalClicks++;
                if (totalClicks > 50) {
                    counter = counter + 4;
                }

                counterField.setText(Integer.toString(++counter));

                currentClicks++;

                if (currentClicks >= randomClicks) {
                    playSound("resources/sound.wav");
                    resetRandomClicks();
                }

                // Увеличение кнопки при нажатии
                cookieButton.setIcon(enlargedIcon);

                // Таймер для возврата к исходному размеру через 0,2 секунды
                Timer timer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        cookieButton.setIcon(originalIcon);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        jPanel.add(cookieButton);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, cookieButton, 0, SpringLayout.HORIZONTAL_CENTER, jPanel);
        springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, cookieButton, 0, SpringLayout.VERTICAL_CENTER, jPanel);

        //3. Counter Label
        JLabel counterLabel = new JLabel("Clicks: ");
        counterLabel.setFont(new Font("Dialog", Font.BOLD, 26));

        jPanel.add(counterLabel);
        springLayout.putConstraint(SpringLayout.WEST, counterLabel, 95, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.NORTH, counterLabel, 550, SpringLayout.NORTH, jPanel);

        //4. Counter Field
        counterField = new JTextField(10);
        counterField.setFont(new Font("Dialog", Font.BOLD, 26));
        counterField.setHorizontalAlignment(SwingConstants.RIGHT);
        counterField.setText("0");
        counterField.setEditable(false);

        jPanel.add(counterField);
        springLayout.putConstraint(SpringLayout.WEST, counterField, 210, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.NORTH, counterField, 550, SpringLayout.NORTH, jPanel);

        //5. Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Dialog", Font.BOLD, 18));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counterField.setText("0");
                resetRandomClicks();
                totalClicks = 0;
            }
        });

        jPanel.add(resetButton);
        springLayout.putConstraint(SpringLayout.WEST, resetButton, 250, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.NORTH, resetButton, 700, SpringLayout.NORTH, jPanel);

        this.getContentPane().add(jPanel);
    }

    private JButton createImageButton(String fileName){
        JButton button;
        try{
            InputStream inputStream = this.getClass().getResourceAsStream(fileName);
            Image image = ImageIO.read(inputStream);
            button = new JButton(new ImageIcon(image));
            button.setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            return button;
        }catch(Exception e){
            System.out.println("Error: " + e);
            return null;
        }
    }

    private JLabel loadImage(String fileName, boolean isResized, int targetWidth, int targetHeight){
        Image image;
        JLabel imageContainer;
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(fileName);
            image = ImageIO.read(inputStream);
            if (isResized) {
                image = resizeImage(image, targetWidth, targetHeight);
            }
            imageContainer = new JLabel(new ImageIcon(image));
            return imageContainer;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    private Image resizeImage(Image image, int targetWidth, int targetHeight) {
        BufferedImage newImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return newImage;
    }
}