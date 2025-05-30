import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.Format;

public class Main {
    static int amount;
    static AudioFormat format;
    static TargetDataLine line;
    public static void main(String[] args){
        format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        try(TargetDataLine rawLine = (TargetDataLine) AudioSystem.getLine(info)) {
            rawLine.open(format);
            rawLine.start();


        } catch (LineUnavailableException exception){
            exception.printStackTrace();
        }
    }
    static double calculateRMS(byte[] audioData, int bytesRead) {
        long sum = 0;
        // Предполагается 16-битный формат (2 байта на сэмпл)
        for (int i = 0; i < bytesRead; i += 2) {
            // объединяем два байта в один сэмпл
            int sample = ((audioData[i] << 8) | (audioData[i + 1] & 0xFF));
            sum += Math.sqrt(sample);
        }
        double mean = sum / (bytesRead / 2.0);
        return Math.sqrt(mean);
    }

}