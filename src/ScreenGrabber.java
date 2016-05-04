import java.awt.Dimension;
import java.awt.Toolkit;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
//ffmpeg -f avfoundation -r 30 -i "1:" -s 1280x720 -q:v 1 output.flv
public class ScreenGrabber {
    private static Frame grabbedFrame;

	public static void main(String[] args) throws Exception {
    	Toolkit kit = Toolkit.getDefaultToolkit();
    	Dimension screenSize = kit.getScreenSize();
    	int screenWidth = screenSize.width;
    	int screenHeight = screenSize.height;
//    	screenWidth = 1920;
//    	screenHeight = 1200;
    	double frameRate = 15.0;
//    	frameRate = 30.0;
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y);
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("1:0");
        
//        grabber.setFormat("x11grab");
        grabber.setFormat("avfoundation");
        grabber.setFrameRate(frameRate);
//        grabber.setVideoBitrate(2500000);
//        grabber.setVideoOption("q:v", "1");
//        grabber.delayedGrab(1);
//        grabber.setPixelFormat(org.bytedeco.javacpp.avutil.AV_PIX_FMT_YUV420P);
//        grabber.setVideoCodec(org.bytedeco.javacpp.avcodec.AV_CODEC_ID_H264);
        grabber.setImageWidth(screenWidth);
        grabber.setImageHeight(screenHeight);
        grabber.start();
        
        int outWidth = 1280;
        int outHeight = 800;
        
        String outputFileName = "rtmp://127.0.0.1:7776/flvplayback/test";
//        String outputFileName = "/Users/qinyuan/Codes/output.flv";
//        FrameRecorder recorder = FrameRecorder.createDefault("/Users/qinyuan/Codes/output.mp4", screenWidth, screenHeight);
        FFmpegFrameRecorder recorder = FFmpegFrameRecorder.createDefault(outputFileName, outWidth, outHeight);
        recorder.setFormat("flv");
        recorder.setFrameRate(frameRate);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        recorder.setVideoQuality(0); // 千万不要设置高质量。。花的你不要不要的。。
//        recorder.setGopSize(60);
        recorder.setVideoOption("x264opts", "keyint=25:min-keyint=25:scenecut=-1");
        recorder.setVideoOption("preset", "fast");
        recorder.setVideoOption("threads", "0");
        
        
        recorder.setAudioCodec(org.bytedeco.javacpp.avcodec.AV_CODEC_ID_AAC);
        recorder.setAudioChannels(2);
//        recorder.setAudioBitrate(128000);
//        recorder.setAudioOption("b:a", "128k");
//        recorder.setVideoBitrate(800000);
//        recorder.setVideoBitrate(1200000);
//          recorder.setVideoBitrate(2500000);
//        recorder.setPixelFormat(org.bytedeco.javacpp.avutil.AV_PIX_FMT_YUV420P);
//        recorder.setPixelFormat(org.bytedeco.javacpp.avutil.AV_PIX_FMT_RGB32);
//        recorder.setPixelFormat(org.bytedeco.javacpp.avutil.AV_PIX_FMT_UYVY422);
        
        recorder.start();

        CanvasFrame frame = new CanvasFrame("Screen Capture", CanvasFrame.getDefaultGamma()/grabber.getGamma());
       
        frame.setSize((int)(screenWidth / 2), (int)(screenHeight / 2));
        while (frame.isVisible()) {
        	grabbedFrame = grabber.grab();
            frame.showImage(grabbedFrame);
//            recorder.record(grabbedFrame);
        }
        frame.dispose();
        recorder.stop();
        grabber.stop();
    }
}