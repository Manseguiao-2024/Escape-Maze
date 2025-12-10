package Code_System;

import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream; 

public class AudioManager {
    
    private Clip backgroundMusic;
    private float musicVolume = 0.8f;
    private float sfxVolume = 0.77f;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    
    private static AudioManager instance;
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    private AudioManager() {
        System.out.println("=== AUDIO MANAGER INITIALIZED ===");
    }
    
    public void playBackgroundMusic(String filePath) {
        try {
            stopBackgroundMusic();
            
            System.out.println("=== LOADING BACKGROUND MUSIC ===");
            System.out.println("📂 Requested path: " + filePath);
            
            // ✅ Try multiple path variations to find the file
            InputStream audioSrc = null;
            String[] pathVariations = {
                filePath,                                    // Original path
                filePath.replace("/assets/Sound/", "/assets.Sound/"),  // Dot notation
                filePath.replace("/assets/Sound/", "/Sound/"),         // Direct Sound folder
                filePath.substring(1) // Remove leading slash
            };
            
            for (String path : pathVariations) {
                System.out.println("🔍 Trying: " + path);
                audioSrc = getClass().getResourceAsStream(path);
                if (audioSrc != null) {
                    System.out.println("✅ Found at: " + path);
                    break;
                }
            }
            
            if (audioSrc == null) {
                System.err.println("❌ Audio file not found after trying all variations");
                System.err.println("💡 Original path: " + filePath);
                System.err.println("💡 Check folder structure: should be src/assets/Sound/ or src/assets.Sound/");
                return;
            }
            
            System.out.println("✅ InputStream created successfully");
            
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            System.out.println("✅ AudioInputStream created");
            
            backgroundMusic = AudioSystem.getClip();
            System.out.println("✅ Clip created");
            
            backgroundMusic.open(audioStream);
            System.out.println("✅ Clip opened");
            
            setVolume(backgroundMusic, musicVolume);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            
            if (musicEnabled) {
                backgroundMusic.start();
                System.out.println("✅ Background music playing: " + filePath);
            }
            System.out.println("================================");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading background music: " + filePath);
            System.err.println("❌ Error type: " + e.getClass().getName());
            System.err.println("❌ Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // *** UPDATED METHOD WITH DEBUGGING ***
    public void playSoundEffect(String filePath) {
        if (!sfxEnabled) {
            return;
        }
        
        new Thread(() -> {
            try {
                System.out.println("[AUDIO] 🔊 Playing sound effect: " + filePath);
                
                // ✅ Try multiple path variations
                InputStream audioSrc = null;
                String[] pathVariations = {
                    filePath,
                    filePath.replace("/assets/Sound/", "/assets.Sound/"),
                    filePath.replace("/assets/Sound/", "/Sound/"),
                    filePath.substring(1)
                };
                
                for (String path : pathVariations) {
                    audioSrc = getClass().getResourceAsStream(path);
                    if (audioSrc != null) {
                        System.out.println("[AUDIO] ✅ Found at: " + path);
                        break;
                    }
                }
                
                if (audioSrc == null) {
                    System.err.println("[AUDIO] ❌ File NOT FOUND: " + filePath);
                    return;
                }
                
                BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

                Clip newClip = AudioSystem.getClip();
                newClip.open(audioStream);
                
                setVolume(newClip, sfxVolume);
                
                newClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        newClip.close();
                        try {
                            audioStream.close();
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                });
                
                newClip.start();
                System.out.println("[AUDIO] ✅ Playing: " + filePath);
                
            } catch (Exception e) {
                System.err.println("[AUDIO] ❌ Error playing sound: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
    
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning() && musicEnabled) {
            backgroundMusic.start();
        }
    }
    
    public void switchBackgroundMusic(String filePath) {
        stopBackgroundMusic();
        playBackgroundMusic(filePath);
    }
    
    // *** UPDATED setVolume WITH ERROR HANDLING ***
    private void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float dB = min + (max - min) * volume;
            volumeControl.setValue(dB);
            System.out.println("[AUDIO] Volume: " + (int)(volume * 100) + "%");
        } catch (Exception e) {
            System.err.println("[AUDIO] Volume control unavailable");
        }
    }
    
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundMusic != null) {
            setVolume(backgroundMusic, musicVolume);
        }
    }
    
    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (musicEnabled) {
            resumeBackgroundMusic();
        } else {
            pauseBackgroundMusic();
        }
    }
    
    public void toggleSFX() {
        sfxEnabled = !sfxEnabled;
        System.out.println("SFX: " + (sfxEnabled ? "ON" : "OFF"));
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean isSFXEnabled() {
        return sfxEnabled;
    }
    
    public void cleanup() {
        stopBackgroundMusic();
    }
}
