package blog.gamedevelopmentbox2dtutorial;

import org.opencv.core.Core;

public class ObjectDetectionDemo {
    public static void main(String[] args) throws InterruptedException {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        boolean face=new FacialRecognition().run();
        System.out.println(face);

    }
}

