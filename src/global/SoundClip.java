package global;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundClip {

	private Clip audio;
	private boolean loop;

	public SoundClip(String filepath) {
		try {
			URL url = this.getClass().getResource(filepath);
			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
			audio = AudioSystem.getClip();
			audio.open(ais);
			audio.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						SoundClip.this.stop();
					}
				}
			});
			setVolume(Settings.VOLUME);
		} catch (UnsupportedAudioFileException e) {
			System.err.println("Audio file format not recognized.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Cannot find file: " + filepath);
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public SoundClip(String filepath, boolean loop) {
		this(filepath);
		this.loop = loop;
	}

	public void play() {
		if (loop)
			audio.loop(Clip.LOOP_CONTINUOUSLY);
		else
			audio.start();
	}

	public void play(int frame) {
		if (frame < 0 || frame > audio.getFrameLength()) {
			throw new IllegalArgumentException("Invalid music position");
		}
		audio.setFramePosition(frame);
		play();
	}

	public void stop() {
		audio.stop();
		audio.setFramePosition(0);
	}

	private float linearToGain(float linear) {
		return (float) (20.0 * Math.log(linear));
	}

	public void setVolume(float f) {
		FloatControl v = (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
		v.setValue(linearToGain(f));
	}

}
