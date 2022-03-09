package blog.gamedevelopmentbox2dtutorial;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import org.mortbay.thread.Timeout;
import org.opencv.core.*;

import org.opencv.core.Point;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

/**
 * FacialRecognition class uses Haar Cascade classification with the help of libraries from openCV
 *Install opencv and make sure you have the right path for  opencv in build.gradle
 * Don't forget to add the opencv-452.dll file to your java sdk before running this class
 */

class FacialRecognition {
    File DATABASE = new File("D:\\DKE-(19-22)\\Year-2\\Project 2-2\\Project-2.2-Group10\\core\\src\\blog\\gamedevelopmentbox2dtutorial\\Database");
    static  {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    }

    public List<Rect> detectAndDisplay(Mat frame, CascadeClassifier faceCascade, CascadeClassifier eyesCascade) {

        boolean detect = false;
        if(frame.empty()) // If Frame is empty stop and return false
        {
            return null;
        }
        Mat frameGray = new Mat(); // Convert the frame to grayscale for easier processing

        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);
        //HighGui.imshow("GrayScale Image", frameGray);
        // -- Detect faces
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(frameGray, faces);
        List<Rect> listOfFaces = faces.toList(); // Store all faces found in a list
        // Check for any faces present in the list
        if (listOfFaces.size() > 0) {
            /*for (Rect face : listOfFaces) {
                Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);

                //Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(255, 0, 255)); // Create a Rectangular frame around detected faces

                Mat faceROI = frameGray.submat(face);
                // -- In each face, detect eyes
                MatOfRect eyes = new MatOfRect();
                eyesCascade.detectMultiScale(faceROI, eyes);
                List<Rect> listOfEyes = eyes.toList();
                /*for (Rect eye : listOfEyes) {
                    Point eyeCenter = new Point(face.x + eye.x + eye.width / 2, face.y + eye.y + eye.height / 2);
                    int radius = (int) Math.round((eye.width + eye.height) * 0.25);
                    //Imgproc.circle(frame, eyeCenter, radius, new Scalar(255, 0, 0), 4); // Create a circular frame around the detected eyes
                }*/
            //}
            //-- Show what you got

            detect = true; // Return true if any face is detected
        }

            //HighGui.imshow("Capture - Face detection", frame);

        return listOfFaces;
    }

    public boolean run() throws InterruptedException {

        String file= ("D:\\DKE-(19-22)\\Year-2\\Project 2-2\\Project-2.2-Group10\\core\\src\\blog\\gamedevelopmentbox2dtutorial\\Database\\Pavan.jpg");
        Mat keypoint=compare_images(file);
        HighGui.imshow("Keypoints", keypoint);
        // Path to the 2 HaarCascades used
        /*Color color = Color.BLUE;
        String filenameFaceCascade = "D:\\DKE-(19-22)\\Year-2\\Project 2-2\\Project-2.2-Group10\\core\\src\\blog\\gamedevelopmentbox2dtutorial\\haarcascade_frontalface_alt.xml";
        String filenameEyesCascade = "D:\\DKE-(19-22)\\Year-2\\Project 2-2\\Project-2.2-Group10\\core\\src\\blog\\gamedevelopmentbox2dtutorial\\haarcascade_eye_tree_eyeglasses.xml";
        int cameraDevice =  0;
        CascadeClassifier faceCascade = new CascadeClassifier();
        CascadeClassifier eyesCascade = new CascadeClassifier();
        if (!faceCascade.load(filenameFaceCascade)) {
            System.err.println("--(!)Error loading face cascade: " + filenameFaceCascade);
            System.exit(0);
        }
        if (!eyesCascade.load(filenameEyesCascade)) {
            System.err.println("--(!)Error loading eyes cascade: " + filenameEyesCascade);
            System.exit(0);
        }
        VideoCapture capture = new VideoCapture(cameraDevice);
        if (!capture.isOpened()) {
            System.err.println("--(!)Error opening video capture");
            System.exit(0);
        }
        Mat frame = new Mat();
        final boolean[] flag = {true};
        //boolean face = false;
        capture.read(frame);
        while (capture.read(frame) ) {

            if (frame.empty()) {
                System.err.println("--(!) No captured frame -- Break!");
                break;
            }
            List<Rect> faces = detectAndDisplay(frame,faceCascade,eyesCascade);
            for( Rect face : faces)
            {

                Mat croppedImage = new Mat(frame, face);
                HighGui.imshow("CroppedImage", croppedImage);
               /* if(identifyFace(croppedImage).equalsIgnoreCase("No match"))
                {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Enter File name");
                    String S = sc.next();
                    saveImage(croppedImage,S);
                }*/
               /* Imgproc.putText(frame, "ID: " + identifyFace(croppedImage), face.tl(), Font.BOLD, 1.5, new Scalar(color.getBlue(), color.getGreen(),color.getRed()));
                Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(color.getBlue(), color.getGreen(),color.getRed()));
            }

            int faceCount = faces.size();
            String message = faceCount + " face" + (faceCount == 1 ? "" : "s") + " detected!";
            Imgproc.putText(frame, message, new Point(3, 25), Font.BOLD, 2, new Scalar(color.getBlue(), color.getGreen(),color.getRed()));
            HighGui.imshow("Face Reg", frame);
            //-- 3. Apply the classifier to the frame
           // boolean detect = detectAndDisplay(frame, faceCascade, eyesCascade);

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    flag[0]= false;
                }
            };

            timer.schedule(task, 5000);
            /*if (detect) {
                face = true;

            }*/
           /* if (HighGui.waitKey(10) == 27) {

                break;// escape
            }
            if (frame.empty()) {
                System.err.println("--(!) No captured frame -- Break!");
                break;
            }

        }

        frame.release();
        HighGui.destroyAllWindows();*/
        return true;

    }
    public Mat compare_images( String filename)
    {
        Mat compareimage = Imgcodecs.imread(filename);
        ORB orb = ORB.create();
        int similarity =0;
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        //orb.detect(currentimage, keypoints1);
        orb.detect(compareimage, keypoints2);

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        //orb.compute(currentimage, keypoints1, descriptors1);
        orb.compute(compareimage, keypoints2, descriptors2);

        /*if (descriptors1.cols() == descriptors2.cols())
        {
            MatOfDMatch matchMatrix = new MatOfDMatch();
            DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING).match(descriptors1, descriptors2, matchMatrix);

            for (DMatch match : matchMatrix.toList())
                if (match.distance <= 50)
                    similarity++;
        }*/

        return descriptors1;
    }

    /*public String identifyFace(Mat image)
    {
        int errorThreshold = 3;
        int mostSimilar = -1;
        File mostSimilarFile = null;

        for (File capture : Objects.requireNonNull(DATABASE.listFiles()))
        {
            int similarities = compare_images(image, capture.getAbsolutePath());

            if (similarities > mostSimilar)
            {
                mostSimilar = similarities;
                mostSimilarFile = capture;
            }
        }

        if (mostSimilarFile != null && mostSimilar > errorThreshold)
        {
            String faceID = mostSimilarFile.getName();
            String delimiter = faceID.contains(" (") ? "(" : ".";
            return faceID.substring(0, faceID.indexOf(delimiter)).trim();
        }
        else
            return "No match";
    }*/
    public void saveImage(Mat image, String filename)
    {
        String extension= ".jpg";
        String filepath= DATABASE+File.separator+filename;
        File destination = new File(filepath+extension);
        if(destination.exists())
        {
            int index=0;
            do
                destination= new File(filepath+"("+index++ + ")"+ extension);
            while (destination.exists());
        }
        Imgcodecs.imwrite(destination.toString(), image);
    }



}



