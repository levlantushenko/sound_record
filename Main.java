import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.Format;

public class Main {
    public static void main(String[] args) {
        boolean isWriting = false;
        int fileCount = 0;
        // Создаем формат аудио
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);

        // Получаем линию для захвата звука
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Линия не поддерживается");
            return;
        }
        double rms = 0;
        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(format);
            line.start();
            byte[] buffer = new byte[1024];
            while(true){
                //getting sound loudness
                int read = line.read(buffer, 0, buffer.length);
                TargetDataLine curLine = (TargetDataLine) AudioSystem.getLine(info);
                if(read > 0) rms = calculateRMS(buffer, read);
                //writing to file
                if(rms > 50 && !isWriting){
                    System.out.println("writing began");
                    isWriting = true;
                    File wavFile = new File("recorded_audio_" + fileCount + ".wav");
                    AudioInputStream ais = new AudioInputStream(curLine);
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
                }else if(rms < 50 && isWriting){
                    System.out.println("writing stopped");
                    isWriting = false;
                    curLine.stop();
                    curLine.close();
                    fileCount++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static double calculateRMS(byte[] audioData, int bytesRead) {
        long sum = 0;
        // Предполагается 16-битный формат (2 байта на сэмпл)
        for (int i = 0; i < bytesRead; i += 2) {
            // объединяем два байта в один сэмпл
            int sample = ((audioData[i] << 8) | (audioData[i + 1] & 0xFF));
            sum += sample * sample;
        }
        double mean = sum / (bytesRead / 2.0);
        return Math.sqrt(mean);
    }
}