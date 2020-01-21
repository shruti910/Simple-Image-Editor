package imageeditor;

/**
 *
 * @author User
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;
import static javax.swing.JFrame.*;


//The WorkImage class acts as a drawing area of the Image Editor program

class WorkImage extends Canvas{ 

  Image img;
  BufferedImage obfimg, bimg, bimg1; 
  float e, angleRad;
  Dimension ds;
  int mX, mY, x, y;
  static boolean imageLoaded;
  boolean isSlided, isResized, isRotated,isDraw, isDrawn;
  MediaTracker mt;
  static Color c;
  Color  colorText;
  Robot rb;
  
  String imgFileName;
  String fontName;
  int fontSize;
  String textToDraw;
  public WorkImage(){

   addMouseListener(new Mousexy()); //to handle mouse event of Canvas class
   addKeyListener(new KeyWork()); //to handle key event of the Canvas
   try{
    rb=new Robot();
   }catch(Exception e){}

   ds=getToolkit().getScreenSize();   
   mX=(int)ds.getWidth()/2; 
   mY=(int)ds.getHeight()/2;
   
  }
  
  @Override
  public void paint(Graphics g){
   Graphics2D g2d=(Graphics2D)g; //create Graphics2D object   
   if(imageLoaded){

    //draw the update image
    if(isSlided || isResized || isRotated || isDrawn ){
     x=mX-bimg.getWidth()/2;
     y=mY-bimg.getHeight()/2;
     g2d.translate(x,y); //move origin to coordinate (x,y)  
     g2d.drawImage(bimg,0,0,null); //draw the image
     
     }
    
    else{ //draw the original image
     x=mX-obfimg.getWidth()/2;
     y=mY-obfimg.getHeight()/2;
     g2d.translate(x,y); //move to  coordinate (x,y)
     g2d.drawImage(obfimg,0,0,null); //draw image
     }
   }
   g2d.dispose(); //clean the Graphic2D object
   
  }

  class Mousexy extends MouseAdapter{
   
   @Override
   public void mousePressed(MouseEvent e){
    Color color=rb.getPixelColor(e.getX(),e.getY()); //get the color at the clicked point
    try{    
    setColor(color); //take the color at the clicked point for later use
    if(isDraw){ //add text to the update image
     if(isSlided || isResized || isRotated || isDrawn)
      addTextToImage(e.getX()-x,e.getY()-y, bimg);
     else  //add text to the original image
      addTextToImage(e.getX()-x,e.getY()-y, obfimg);
      
      
     }

    }catch(Exception ie){}
    
    
   }
   }

 //The KeyWork class extends the KeyAdpater class to implement the keyPressed method
 //to handle the key event of the Canvas
 class KeyWork extends KeyAdapter{
  public void keyPressed(KeyEvent e){
   if(e.getKeyCode()==27){ //ESC is pressed to stop drawing the text on the image
   isDraw=false;
    textToDraw="";
    fontName="";
    fontSize=0;
    }
   }
  }
 
 
  //set the selected color to the c variable
  public void setColor(Color color){
   c=color;   
  }
  //set the image filename to the imgFileName variable
  public void setImgFileName(String fname){
   imgFileName=fname;
  }
  //initialize variables
  public void initialize(){
   imageLoaded=false;
    isDraw=false;
   isSlided=false;
   isResized=false;
  isRotated=false;
 isDrawn=false;
 c=null;
   angleRad=0.0f;
   e=0.0f;
   }

  //reset the drawing area if 'cancel editing' is pressed
  public void reset(){
   if(imageLoaded){
   loadImage(imgFileName);
   repaint();
   }
   
  }
  
   //Prepare the image so it is ready to display and editable
   public void loadImage(String filename){
   initialize();
   try{
  
   mt=new MediaTracker(this);      //track the image loading
   img=Toolkit.getDefaultToolkit().getImage(filename); 
   mt.addImage(img,0);
    mt.waitForID(0); 
   //get the image width and height  
   int width=img.getWidth(null);
   int height=img.getHeight(null);
   //create buffered image from the image so any change to the image can be made
   obfimg=createBufferedImageFromImage(img,width,height,false);
   //create the blank buffered image
   //the update image data is stored in the buffered image   
   bimg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);  
   imageLoaded=true; //now the image is loaded
   }catch(Exception e){System.exit(-1);}
  }

  /*The filterImage method applies brightness to the image when the knob of the image slider is 
  making change.
  When the value of the image slider changes it affects the e variable
  so the image is brighter or darker*/
  public void filterImage(){ 
   float[] elements = {0.0f, 1.0f, 0.0f, -1.0f,e,1.0f,0.0f,0.0f,0.0f};
       
 
   Kernel kernel = new Kernel(3, 3, elements);  //create kernel object to encapsulate the elements array
   ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null); //create ConvolveOp to encapsulate 
   //the kernel
   bimg= new BufferedImage(obfimg.getWidth(),obfimg.getHeight(),BufferedImage.TYPE_INT_RGB);
   cop.filter(obfimg,bimg); //start filtering the image 
   //the filtered image is stored in the bimg buffered image
   //now the image increases or decreases its brightness
   
   //RescaleOp
  }
  
  public void addTextToImage(int x,int y, BufferedImage img){
  //create a blank buffered image
  BufferedImage bi=(BufferedImage)createImage(img.getWidth(),img.getHeight());
  //create the Graphics2D object from the buffered image   
  Graphics2D  g2d=(Graphics2D)bi.createGraphics();
  g2d.setFont(new Font(fontName,Font.BOLD,fontSize));
  g2d.setPaint( colorText);
  g2d.drawImage(img,0,0,null);
  g2d.drawString(textToDraw,x,y);
  bimg=bi; //update the image
  isDrawn=true; //there is a text drawing on the image
  g2d.dispose();
  repaint();    //redisplay the update image so the text is displayed on the image now
  }

  //Rotate the image shown on the program interface
  public void LeftImageRotate(BufferedImage image,int w,int h){
   
   BufferedImage bi=(BufferedImage)createImage(w,h);
   Graphics2D  g2d=(Graphics2D)bi.createGraphics(); 
 
   angleRad=(float)Math.PI/2;     
   g2d.translate(w/2,h/2); 
   g2d.rotate(angleRad); 
   g2d.translate(-h/2,-w/2); 
   g2d.drawImage(image,0,0,null); //draw the rotated image
   bimg=bi;
   g2d.dispose();
   }
    public void RightImageRotate(BufferedImage image,int w,int h){
   
   BufferedImage bi=(BufferedImage)createImage(w,h);
   Graphics2D  g2d=(Graphics2D)bi.createGraphics(); 
   angleRad=(float)(-(Math.PI/2));     
   g2d.translate(w/2,h/2); 
   g2d.rotate(angleRad); 
   g2d.translate(-h/2,-w/2); 
   g2d.drawImage(image,0,0,null); //draw the rotated image
   bimg=bi;
   g2d.dispose();  
   
   
  }
 
  public void rotateImage(int a){
    BufferedImage bi;
   
    if(isSlided || isResized || isRotated || isDrawn){
     bi=bimg;     
    }
    
    else{
     bi=obfimg;
    }
if(a==0)
    LeftImageRotate(bi,bi.getHeight(),bi.getWidth());
if(a==1)
    RightImageRotate(bi,bi.getHeight(),bi.getWidth());
       }
  
  public void resizeImage(int w,int h){
    BufferedImage bi=(BufferedImage)createImage(w,h);
    Graphics2D g2d=(Graphics2D)bi.createGraphics();
    //resize the update image
 
    if(isSlided || isRotated ||isDrawn)
     g2d.drawImage(bimg,0,0,w,h,null);
    //resize the original image
    else
     g2d.drawImage(obfimg,0,0,w,h,null);
    bimg=bi;
    g2d.dispose();
   
  }
  //set a value to e variable 
  //this method is invoked when the user makes change to the  image slider
  public void setValue(float value){ 
   e=value;
  }
  
  //Set a boolean value the isSlided variable 
  public void setActionSlided(boolean value ){ 
   isSlided=value;
  }
  //Set a boolean value the isResized variable   
  public void setActionResized(boolean value ){ 
   isResized=value;
  } 
     
 
  //Set a boolean value to the isDraw variable   
  public void setActionDraw(boolean value ){ 
  isDraw=value;
   
  }
  
  public void setActionRotate(boolean value ){ 
  isRotated=value;
   
  }
  
 //The createBufferedImageFromImage method is abled to generate a buffered image from an input image
 public BufferedImage createBufferedImageFromImage(Image image, int width, int height, boolean tran)
   { BufferedImage dest ;
  if(tran) 
       dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  else
   dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
       Graphics2D g2 = dest.createGraphics();
       g2.drawImage(image, 0, 0, null);
       g2.dispose();
       return dest;
   }

 public void saveToFile(String filename){
  String ftype=filename.substring(filename.lastIndexOf('.')+1); //to get extension of image
  try{
  
   //save the updated image
   if(isSlided || isResized ||  isRotated || isDrawn)
    ImageIO.write(bimg,ftype,new File(filename));
     }catch(IOException e){System.out.println("Error in saving the file");}
  }
 //Assign values to the variables used in drawing text on the image
 public void setText(String text,String fName, int fSize, Color color){
   textToDraw=text;
   fontName=fName;
   fontSize=fSize;
   if(color==null)
     colorText=new Color(0,0,0);
   else
     colorText=color;
  }
}

////end of the WorkImage class

////start the Main class
//The Main class represents the main program interface
//In this interface, you can open the image file, save the update image, and edit the image 

class  Main extends JFrame implements ActionListener{
 
 WorkImage wi;
 JFileChooser chooser; 
 JMenuBar mainmenu;
 JMenu menu;
 JMenu editmenu;
 JMenuItem mopen;
 JMenuItem msaveas;
 JMenuItem msave;
 JMenuItem mexit; 
 JMenuItem mbright; 
 
 JMenuItem mresize;
 JMenuItem mrotate;
 
 JMenuItem maddtext;
 JMenuItem mcancel;
 String filename;
 Main(){
  wi=new WorkImage();
  Container cont=getContentPane();
  cont.setBackground(Color.GRAY);
  cont.add(wi,BorderLayout.CENTER );  
  mainmenu=new JMenuBar();
  menu=new JMenu("File");
  menu.setMnemonic(KeyEvent.VK_F);

  mopen=new JMenuItem("Open...");
  mopen.setMnemonic(KeyEvent.VK_O);
  mopen.addActionListener(this);

  msaveas=new JMenuItem("Save as...");
  msaveas.setMnemonic(KeyEvent.VK_S);
  msaveas.addActionListener(this);

  msave=new JMenuItem("Save");
  msave.setMnemonic(KeyEvent.VK_V);
  msave.addActionListener(this);  

  mexit=new JMenuItem("Exit");
  mexit.setMnemonic(KeyEvent.VK_X);
  mexit.addActionListener(this);
  menu.add(mopen);
  menu.add(msaveas);
  menu.add(msave);
  menu.add(mexit);  

  editmenu=new JMenu("Edit");
  editmenu.setMnemonic(KeyEvent.VK_E);
  mbright=new JMenuItem("Image brightness");
  mbright.setMnemonic(KeyEvent.VK_B);
  mbright.addActionListener(this);

  maddtext=new JMenuItem("Add text on image");
  maddtext.setMnemonic(KeyEvent.VK_A);
  maddtext.addActionListener(this);  

  mresize=new JMenuItem("Image resize");
  mresize.setMnemonic(KeyEvent.VK_R);
  mresize.addActionListener(this);
 
  

  mrotate=new JMenuItem("Image rotation");
  mrotate.setMnemonic(KeyEvent.VK_T);
  mrotate.addActionListener(this);

 
 
  mcancel=new JMenuItem("Cancel editing");
  mcancel.setMnemonic(KeyEvent.VK_X);
  mcancel.addActionListener(this);

  editmenu.add(maddtext);
  editmenu.add(mbright);
  
  editmenu.add(mresize);
  editmenu.add(mrotate);
  
  editmenu.add(mcancel);

  mainmenu.add(menu);
  mainmenu.add(editmenu);
  setJMenuBar(mainmenu);
 
  setTitle("Image Editor");
  setDefaultCloseOperation(EXIT_ON_CLOSE);
  setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH); 
     setVisible(true); 

  chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif","bmp","png", "jpeg");
      chooser.setFileFilter(filter);
      chooser.setMultiSelectionEnabled(false);
  enableSaving(false);
  wi.requestFocus();//to make it the main focus area
  }

 ////start the ImageBrightness class
 //The ImageBrightness class represents the interface to allow the user to make the image 
 //brighter or darker by changing the value of the image slider
 //The ImageBrightness class is in the Main class
  public class ImageBrightness extends JFrame implements ChangeListener{
  JSlider slider; 
 
  ImageBrightness(){ 
  addWindowListener(new WindowAdapter(){
     @Override
     public void windowClosing(WindowEvent e){     
      dispose();
      
     }
    });
  Container cont=getContentPane(); 
  cont.setBackground(Color.cyan);
  slider=new JSlider(-10,10,0); 
  slider.setEnabled(false);
  slider.addChangeListener(this);
  cont.add(slider,BorderLayout.CENTER); 
  slider.setEnabled(true);
  setTitle("Image brightness");
  setPreferredSize(new Dimension(300,100));
  setVisible(true);
  pack();
  enableSlider(false);
  }
  public void enableSlider(boolean enabled){
   slider.setEnabled(enabled);
  }
  public void stateChanged(ChangeEvent e){
    wi.setValue(slider.getValue()/10.0f);
    wi.setActionSlided(true);   
    wi.filterImage();
    wi.repaint();
    enableSaving(true);
   
  }

 } ////end of the ImageBrightness class

 ////start the ImageResize class
 
 public class ImageResize extends JFrame implements ActionListener {
  JPanel panel;
  JTextField txtWidth;
  JTextField txtHeight;
  JButton btOK;
  ImageResize(){
  setTitle("Image resize");

  setPreferredSize(new Dimension(400,100));
  
  btOK=new JButton("OK");
  btOK.setBackground(Color.BLACK);
  btOK.setForeground(Color.PINK);  
  btOK.addActionListener(this);

  txtWidth=new JTextField(4);
  txtWidth.addKeyListener(new KeyList());
  txtHeight=new JTextField(4);
  txtHeight.addKeyListener(new KeyList());
  panel=new JPanel();
  panel.setLayout(new FlowLayout());
  panel.add(new JLabel("Width:"));
  panel.add(txtWidth);
  panel.add(new JLabel("Height:"));
  
  panel.add(txtHeight);
  panel.add(btOK);
  panel.setBackground(Color.blue);
  add(panel, BorderLayout.CENTER);
  setVisible(true);
  pack();
  enableComponents(false);
  }
  //This method can be invoked to  enable the text boxes of image width and height
  public void enableComponents(boolean enabled){
   txtWidth.setEnabled(enabled); 
   txtHeight.setEnabled(enabled);
   btOK.setEnabled(enabled);
  }
  //This method works when you click the OK button to resize the image
  public void actionPerformed(ActionEvent e){
   if(e.getSource()==btOK){
    wi.setActionResized(true);     
    wi.resizeImage(Integer.parseInt(txtWidth.getText()),Integer.parseInt(txtHeight.getText()));
    enableSaving(true);
    wi.repaint();
    }
  }
  //Restrict the key presses
  //Only number, backspace, and delete keys are allowed
  public class KeyList extends KeyAdapter{
     public void keyTyped(KeyEvent ke){
 
    char c = ke.getKeyChar(); 
    int intkey=(int)c;
    if(!(intkey>=48 && intkey<=57 || intkey==8 || intkey==127)) //(0-9), backspace and delete keys
     {
     ke.consume(); //hide the unwanted key   
  
      }  
     
   }
  
  } 
 }////end of the ImageResize class

 ////start the TextAdd class

 public class TextAdd extends JFrame implements ActionListener {
  JPanel panel;
  JTextArea txtText;
  JComboBox cbFontNames;
  JComboBox cbFontSizes;
  JButton btOK;
  JButton btSetColor;
  String seFontName;
  Color colorText;
  int seFontSize;
  TextAdd(){
  colorText=null;
  setTitle("Add text to the image");
  
  setPreferredSize(new Dimension(400,150));
  
  btOK=new JButton("OK");
  btOK.setBackground(Color.BLACK);
  btOK.setForeground(Color.BLUE);  
  btOK.addActionListener(this);

  btSetColor=new JButton("Set text color");
  btSetColor.setBackground(Color.BLACK);
  btSetColor.setForeground(Color.WHITE);  
  btSetColor.addActionListener(this);

  txtText=new JTextArea(1,30);
  cbFontNames=new JComboBox();
  cbFontSizes=new JComboBox();
  panel=new JPanel();
  panel.setLayout(new GridLayout(4,1));
  panel.add(new JLabel("Text:"));
  panel.add(txtText);
  panel.add(new JLabel("Font Name:"));  
  panel.add(cbFontNames);
  panel.add(new JLabel("Font Size:"));  
  panel.add(cbFontSizes);
  panel.add(btSetColor);
  panel.add(btOK);
  panel.setBackground(Color.GRAY);
  add(panel, BorderLayout.CENTER);
  setVisible(true);
  pack(); 
  listFonts();
  }

  
  public void actionPerformed(ActionEvent e){
   if(e.getSource()==btOK){ //the button OK is clicked so the text is ready to place on the image
    wi.setActionDraw(true); 
    String textDraw=txtText.getText(); 
    String fontName=cbFontNames.getSelectedItem().toString();
    int fontSize=Integer.parseInt(cbFontSizes.getSelectedItem().toString());
    wi.setText(textDraw,fontName,fontSize,colorText);
    dispose();
    }
   else if(e.getSource()==btSetColor){ //show color chooser dialog for color selection
    JColorChooser jser=new JColorChooser();   
    colorText=jser.showDialog(this,"Color Chooser",Color.RED);
     enableSaving(true);
   }
  }
  
  //The listFonts method get all available fonts from the system 
  
  public void listFonts(){
   //get the available font names and add them to the font names combobox
   GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment(); 
   String[] fonts=ge.getAvailableFontFamilyNames();
  
   for ( int i = 0; i < fonts.length; i++ )
    {
      cbFontNames.addItem(fonts[i]);
    }
   //Initialize font sizes
   for(int i=8;i<50;i++)
    cbFontSizes.addItem(i);
   
  }
 } ////end of the TextAdd class
 
 public class ImageRotate extends JFrame implements ActionListener {
  JPanel panel;
  
  JButton btLeft,btRight;
  ImageRotate(){
  setTitle("Image rotate");

  setPreferredSize(new Dimension(400,100));
  
  btLeft=new JButton("Rotate Left");
  btLeft.setBackground(Color.BLACK);
  btLeft.setForeground(Color.PINK);  
  btLeft.addActionListener(this);
  
  btRight=new JButton("Rotate Right");
  btRight.setBackground(Color.BLACK);
  btRight.setForeground(Color.PINK);  
  btRight.addActionListener(this);
  panel=new JPanel();
  panel.setLayout(new FlowLayout());
 
  panel.add(btLeft);
  panel.add(btRight);
  panel.setBackground(Color.blue);
  add(panel, BorderLayout.CENTER);
  setVisible(true);
  pack();
  
  }

        @Override
        public void actionPerformed(ActionEvent e) {
            int a;
           if(e.getSource()==btLeft){
               a=0;
         wi.rotateImage(a);
   wi.setActionRotate(true); 
    enableSaving(true);
    wi.repaint();
    }
           if(e.getSource()==btRight){
               a=1;
         wi.rotateImage(a);
   wi.setActionRotate(true);
    enableSaving(true);
    wi.repaint();
           
        }
 }
 }
  

 //****handling events of sub-menu items on the main program interface*************
 public void actionPerformed(ActionEvent e){

  JMenuItem source = (JMenuItem)(e.getSource());
  if(e.getSource()==mopen)
    {
    openImage();
    wi.repaint();
                                                      
      
     }
  else if(e.getSource()==msaveas)
    {
    showSaveFileDialog(); 
      
     }
  else if(e.getSource()==msave)
    {
     
    wi.saveToFile(filename);  
     }
  else if(e.getSource()==maddtext)
    {
    new TextAdd(); 
    }

  else if(e.getSource()==mbright)
    {
     
    ImageBrightness ib=new ImageBrightness(); 
    if(WorkImage.imageLoaded)
     ib.enableSlider(true); 
     }
  
  
  else if(e.getSource()==mresize)
    {
     
    ImageResize ir=new ImageResize();
    if(WorkImage.imageLoaded)
     ir.enableComponents(true);  
     }
  else if(e.getSource()==mrotate)
    {
     
    if(WorkImage.imageLoaded){
     new ImageRotate();
     enableSaving(true);
     } 
    }
 else if(e.getSource()==mcancel) {
    wi.setImgFileName(filename);
    wi.reset();
    }
  
  else if(e.getSource()==mexit) 
    System.exit(0);
     
    
  } 
      
 //The openImage method has code to open the file dialog so the user can choose
 //the file to show on the program interface

 public void openImage(){
  
  int userSelection = chooser.showOpenDialog(this);
      if(userSelection == JFileChooser.APPROVE_OPTION) {   
   filename=chooser.getSelectedFile().toString();
   wi.loadImage(filename);
   }
           
  }

 //The showSaveFileDialog method has code to display the save file dialog
 //It is invoked when the user selects Save as... sub-menu item
 public void showSaveFileDialog(){
      int userSelection = chooser.showSaveDialog(this);
      if(userSelection == JFileChooser.APPROVE_OPTION) {  
   String filen=chooser.getSelectedFile().toString(); 
                wi.saveToFile(filen);  
            
            }
 }


 //The enableSaving method defines code to enable or  disable saving sub-menu items
 public void enableSaving(boolean f){
  msaveas.setEnabled(f);
  msave.setEnabled(f); 
  
  }

 } ////end of the Main class




public class ImageEditor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         Main mn=new Main();
    }
    
}

