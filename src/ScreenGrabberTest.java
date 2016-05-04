import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.bytedeco.javacv.*;

import com.sun.prism.Image;

import org.bytedeco.javacpp.*;
//ffmpeg -f avfoundation -r 30 -i "1:" -s 1280x720 -q:v 1 output.flv


public class ScreenGrabberTest {
	private static Frame grabbedFrame;

	public static void main(String[] args) throws Exception {
		Controller controller = new Controller();
		Settings settings = controller.getSettings();
		
		
    	Toolkit kit = Toolkit.getDefaultToolkit();
    	Dimension screenSize = kit.getScreenSize();
    	int screenWidth = screenSize.width;
    	int screenHeight = screenSize.height;
    	
    	double frameRate = Double.parseDouble(settings.getProperty("frameRate"));
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("1:");
        grabber.setFormat("avfoundation");
        grabber.setFrameRate(frameRate);
        grabber.setImageWidth(screenWidth);
        grabber.setImageHeight(screenHeight);
        grabber.start();
        CanvasFrame frame = new CanvasFrame("Screen Capture", CanvasFrame.getDefaultGamma()/grabber.getGamma());
        frame.setCanvasSize(screenWidth/2, screenHeight/2);
        frame.setController(controller);
        while (frame.isVisible()) {
        	grabbedFrame = grabber.grab();
            frame.showImage(grabbedFrame);
            controller.recorder(grabbedFrame);
        }
        frame.dispose();
        controller.clean();
        grabber.stop();
    }
}


class Recorder
{
	public static FFmpegFrameRecorder getRecorder(String outputFileName, double frameRate, int outWidth, int outHeight) {
        FFmpegFrameRecorder recorder = null;
		try {
			recorder = FFmpegFrameRecorder.createDefault(outputFileName, outWidth, outHeight);	
			recorder.setFormat("flv");
			
	        recorder.setFrameRate(frameRate);
	        
//	        recorder.setVideoOption("crf", "28"); //where 0 is lossless, 23 is default
	        
	        recorder.setVideoOption("tune", "zerolatency");
	        recorder.setVideoOption("preset", "fast");
	        recorder.setVideoOption("fflags", "nobuffer");
	        recorder.setVideoOption("analyzeduration", "0");
	        
	        
	        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//	        recorder.setVideoQuality(0); // 千万不要设置高质量。。花的你不要不要的。。
	        recorder.setVideoOption("x264opts", "keyint=25:min-keyint=25:scenecut=-1");
	        
	        recorder.setVideoOption("threads", "0");
	        
	        
	        
//	        recorder.setAudioCodec(org.bytedeco.javacpp.avcodec.AV_CODEC_ID_AAC);
//	        recorder.setAudioChannels(2);
	        return recorder;
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}

class Controller
{
	
    private static Settings settings = Settings.getSettingInstance();
    private static FFmpegFrameRecorder rtmpRecorder = null;
    private static FFmpegFrameRecorder fileRecorder = null;
    private static int isStreaming = 0;
    private static int isRecording = 0;
    
    public int getStreamingState() {
		return isStreaming;
	}
    
    public int getRecordingState() {
		return isRecording;
	}
    
    public Settings getSettings()
    {
    	return settings;
    }
    
    public void recorder(Frame frame) throws org.bytedeco.javacv.FrameRecorder.Exception {
		if (isStreaming == 1){
			rtmpRecorder.record(frame);
		}
		if (isRecording == 1){
			fileRecorder.record(frame);
		}
	}
    
    public void startFileRecorder() {
    	String outputFile = settings.getProperty("outputFile");
    	double frameRate = Double.parseDouble(settings.getProperty("frameRate"));
    	int outWidth = Integer.parseInt(settings.getProperty("outputWidth"));
        int outHeight = Integer.parseInt(settings.getProperty("outputHeight"));
        if (outputFile != null)
        {
        	Date now = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd-HHmmss");
            if (!outputFile.endsWith("/")){
            	outputFile = outputFile + "/";
            }
        	String outputFileName = outputFile + ft.format(now) + ".flv";
        	System.out.println(outputFileName);
        	fileRecorder = Recorder.getRecorder(outputFileName, frameRate, outWidth, outHeight);
        	if (fileRecorder != null){
            	try {
					fileRecorder.start();
					isRecording = 1;
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}
    
    public void stopFileRecorder() {
    	isRecording = 0;
		if (fileRecorder != null){
			try {
				fileRecorder.stop();				
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileRecorder = null;
		}
	}
    
    public void startRtmpRecorder() {
    	String url = settings.getProperty("url");
    	double frameRate = Double.parseDouble(settings.getProperty("frameRate"));
    	int outWidth = Integer.parseInt(settings.getProperty("outputWidth"));
        int outHeight = Integer.parseInt(settings.getProperty("outputHeight"));
        if (url != null)
        {
        	rtmpRecorder = Recorder.getRecorder(url, frameRate, outWidth, outHeight);
        }

        if (rtmpRecorder != null){
        	try {
				rtmpRecorder.start();
				isStreaming = 1;
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
    
    public void stopRtmpRecorder() {
    	isStreaming = 0;
		if (rtmpRecorder != null){
			try {
				rtmpRecorder.stop();
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			rtmpRecorder = null;
		}
	}
    
    public void clean() {
    	if (rtmpRecorder != null){
        	try {
				rtmpRecorder.stop();
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if (fileRecorder != null){
        	try {
				fileRecorder.stop();
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}