import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.Format;

public class Main {
    public static void main(String[] args){
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try(TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(format);
            line.start();
            Thread record = new Thread(() ->
            {try {
                File file = new File("recorded_audio.wav");
                AudioInputStream stream = new AudioInputStream(line);
                AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
            }catch (IOException e){
                e.printStackTrace();
            }
            });
            record.start();
            int a = System.in.read();
            line.stop();
            line.close();
        } catch (LineUnavailableException | IOException exception){
            exception.printStackTrace();
        }
    }
}