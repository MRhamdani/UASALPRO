package uas;

import java.util.Scanner;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

public class UASFINAL {
    private static final char[] SYMBOLS = {'1', '2', '3', '4', '5'};
    private static final int JACKPOT = 3;
    private static final int INITIAL_BALANCE = 50;

    private static int balance = INITIAL_BALANCE;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════╗");
        System.out.println("║======== WARNING! ========║");
        System.out.println("║==== JANGAN MAIN SLOT ====║");
        System.out.println("║== APALAGI ANDA MISKIN! ==║");
        System.out.println("║= DAPUR RUMAH ANDA HARUS =║");
        System.out.println("║==== TETEP NGEBUL!!!! ====║");
        System.out.println("╚══════════════════════════╝");
        System.out.println("\nSaldo awal Anda: " + balance + " koin\n");

        while (balance > 0) {
            System.out.println("-----------------------------------------");
            int bet = getBetAmount();

            if (bet == 0) {
                System.out.println("Terima kasih telah membuat saya tambah kaya!");
                System.out.println(" ");
                break;
            }

            balance -= bet;
            char[] result = generateSpinResult();

            showSpinningAnimation();
            displaySlots(result);

            int winnings = calculateWinnings(result, bet);
            balance += winnings;

            showResultMessage(winnings, bet);
            System.out.println("Saldo Anda saat ini: " + balance + " koin\n");
        }

        if (balance <= 0) {
            System.out.println("------------------------");
            System.out.println("HAHAHA!!! AYO DEPO LAGI.");
            System.out.println("GADAIKAN SEMUA BARANG BERHARGA ANDA UNTUK MEMBUAT SAYA TERUS BERTAMBAH KAYA!!!");
            System.out.println("HAHAHAHHA");
            System.out.println(" ");
        }
    }

    private static int getBetAmount() {
        while (true) {
            System.out.print("Masukkan jumlah taruhan (0 untuk keluar): ");
            try {
                int bet = Integer.parseInt(scanner.nextLine());
                if (bet == 0) {
                    return bet;
                } else if (bet < 0) {
                    System.out.println("Yang bener dong ah! masa kurang?!");
                } else if (bet > balance) {
                    System.out.println("HEY! Saldo Anda: " + balance + " koin. BERARTI KURANG DONG!");
                } else {
                    return bet;
                }
            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang valid!");
            }
        }
    }

    private static char[] generateSpinResult() {
        char[] result = new char[3];
        for (int i = 0; i < 3; i++) {
            int randomIndex = (int) (Math.random() * SYMBOLS.length);
            result[i] = SYMBOLS[randomIndex];
        }
        return result;
    }

    private static Synthesizer synth;
    private static MidiChannel[] channels;

    static {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
        } catch (Exception e) {
            System.out.println("Sound system error: " + e.getMessage());
        }
    }

    private static void playSoundEffect(int note, int duration, int volume) {
        try {
            channels[0].noteOn(note, volume);
            Thread.sleep(duration);
            channels[0].noteOff(note);
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    private static void playSpinSound() {
        try {
            // Set instrumen ke synthesizer (51)
            channels[0].programChange(51);
        
            // Efek reverb
            channels[0].controlChange(91, 115);
        
            // Pattern nada spiral naik dengan durasi semakin cepat
            int[] spinNotes = {60, 63, 66, 69, 72, 75, 78, 81}; // C4 - A5 (arpeggio minor)
            int[] durations = {100, 70, 50, 60, 100, 70, 50, 60};
        
            // Mainkan dengan efek panning bergerak
            for(int i = 0; i < spinNotes.length; i++) {
                // Geser panning dari kiri ke kanan
                channels[0].controlChange(10, i * 16); // 0-127
            
                // Mainkan nada dengan velocity semakin keras
                channels[0].noteOn(spinNotes[i], 30 + (i * 30));
                Thread.sleep(durations[i]);
                channels[0].noteOff(spinNotes[i]);
            
                // Tambahkan efek drum pada channel 10 (channel perkusi)
                if(i % 2 == 0) {
                    channels[9].noteOn(42, 100); // Closed Hi-Hat
                    Thread.sleep(50);
                    channels[9].noteOff(42);
                }
            }
        
            // Akhiran dengan slide pitch down
            channels[0].setPitchBend(8192); // Reset pitch bend
            channels[0].noteOn(84, 90); // C6
            channels[0].setPitchBend(0); // Pitch turun maksimal
            Thread.sleep(100);
            channels[0].noteOff(84);
            channels[0].setPitchBend(8192); // Reset kembali
        
        } catch (Exception e) {
            System.out.println("Error sound: " + e.getMessage());
    }
}

    private static void showSpinningAnimation() throws InterruptedException {
    System.out.println("\n");
    for (int i = 0; i < 3; i++) {
        playSpinSound();
        
        // Membangun garis untuk setiap kotak
        StringBuilder topLine = new StringBuilder();
        StringBuilder middleLine = new StringBuilder();
        StringBuilder bottomLine = new StringBuilder();
        
        for (int j = 0; j <= i; j++) {
            topLine.append("╔═══╗").append(j < i ? " " : "");
            middleLine.append("║ ? ║").append(j < i ? " " : "");
            bottomLine.append("╚═══╝").append(j < i ? " " : "");
        }

        // Cetak dengan ANSI escape code untuk overwrite
        if(i > 0) System.out.print("\033[3A"); // Pindah kursor ke atas
        
        System.out.println(topLine);
        System.out.println(middleLine);
        System.out.println(bottomLine);
        
        Thread.sleep(300); // Sesuaikan kecepatan animasi
    }
}


    private static void displaySlots(char[] result) throws InterruptedException {
        System.out.println("\nHasil:");
        System.out.println("╔═══╗ ╔═══╗ ╔═══╗");
        System.out.println("║ " + result[0] + " ║ ║ " + result[1] + " ║ ║ " + result[2] + " ║");
        System.out.println("╚═══╝ ╚═══╝ ╚═══╝");
        Thread.sleep(800);
    }

    private static int calculateWinnings(char[] result, int bet) {
        if (result[0] == result[1] && result[1] == result[2]) {
            return bet * JACKPOT; // Jackpot
        } else if (result[0] == result[1] || result[1] == result[2] || result[0] == result[2]) {
            return bet + 5; // Dua simbol cocok
        }
        return 0; // RUNGKAD
    }

    private static void showResultMessage(int winnings, int bet) {
        if (winnings == bet * JACKPOT) {
            playVictoryFanfare();
            System.out.println("\nJACKPOT! Anda menang " + winnings + " koin!");
        } else if (winnings > 0) {
            playSuccessSound();
            System.out.println("\nNih kesian! Anda menang " + winnings + " koin!");
        } else {
            playFailSound();
            System.out.println("\nRUNGKAD!!! Anda kehilangan " + bet + " koin.");
        }
    }

    private static void playVictoryFanfare() {
        try {
            // Set instrumen trumpet untuk channel 0
            channels[0].programChange(56); // Trumpet
            channels[1].programChange(48); // String Ensemble
            channels[2].programChange(0);  // Piano
            channels[9].programChange(0);  // Channel 10 untuk perkusi

            // Mainkan fanfare dengan chord
            int[] melody = {72, 76, 79, 84}; // C5, E5, G5, C6
            int[] chords = {60, 64, 67};      // C4, E4, G4
        
            // Efek tremolo strings
            channels[1].controlChange(92, 65); // Efek reverb
            channels[1].controlChange(93, 30); // Chorus
        
            // Mainkan chord
            for(int note : chords) {
                channels[1].noteOn(note, 90);
            }
        
            // Melodi trumpet dengan pitch bend
            channels[0].setPitchBend(8192); // Reset pitch bend
            for(int i = 0; i < melody.length; i++) {
                // Tambahkan vibrato
                channels[0].controlChange(1, 80 + (i*10));
            
                // Mainkan nada dengan pitch bend naik
                channels[0].setPitchBend(9000);
                channels[0].noteOn(melody[i], 100);
                Thread.sleep(150);
                channels[0].setPitchBend(8192); // Reset
            
                // Drum roll
                channels[9].noteOn(38, 120);  // Snare drum
                Thread.sleep(50);
                channels[9].noteOff(38);
            }
        
            // Akhir dengan cymbal crash dan timpani
            channels[9].noteOn(49, 120);     // Crash cymbal
            channels[9].noteOn(47, 110);     // Timpani
            Thread.sleep(500);
        
            // Matikan semua nada
            channels[0].allNotesOff();
            channels[1].allNotesOff();
            channels[9].allNotesOff();
        
        } catch (Exception e) {
            System.out.println("Error playing fanfare: " + e.getMessage());
        }
    }

    private static void playSuccessSound() {
        try {
            // Set instrumen bell dan harp
            channels[3].programChange(14); // Tubular Bells
            channels[4].programChange(46); // Harp
        
            // Efek gemerincing koin
            channels[3].controlChange(93, 60); // Chorus
            channels[3].noteOn(84, 90);        // C6
            channels[4].noteOn(60, 80);        // C4
        
            // Glissando harp
            for(int i = 60; i < 72; i++) {
                channels[4].noteOn(i, 50);
                Thread.sleep(30);
                channels[4].noteOff(i);
            }
        
            Thread.sleep(300);
            channels[3].allNotesOff();
            channels[4].allNotesOff();
        
        } catch (Exception e) {
            System.out.println("Error playing success sound: " + e.getMessage());
        }
    }

    private static void playFailSound() {
        try {
            // Setup channel dan instrumen
            channels[0].programChange(33);    // Fretless Bass (suara dasar original)
            channels[1].programChange(116);   // Guitar Fret Noise (efek gesekan)
            channels[2].programChange(127);   // Gunshot (efek dramatis)
            channels[9].programChange(0);     // Channel perkusi

            // Mainkan nada original dengan variasi
            channels[0].controlChange(5, 90);        // Portamento time
            channels[0].controlChange(65, 127);      // Enable portamento
        
            // Layer 1: Nada dasar original dengan slide
            channels[0].noteOn(48, 90); // C2
            channels[0].noteOn(44, 90); // G#1 (minor 6th interval)
            Thread.sleep(200);
        
            // Layer 2: Efek dissonan yang naik
            channels[1].controlChange(93, 127); // Chorus maksimal
            for(int i = 48; i < 60; i++) {
                channels[1].noteOn(i, 30); // Efek "kawat bergemerincing"
                Thread.sleep(20);
            }
        
            // Layer 3: Efek "jatuh" dengan pitch bend
            channels[0].setPitchBend(0); // Turunkan pitch maksimal
            Thread.sleep(500);
        
            // Layer 4: Efek crash perkusi kompleks
            channels[9].noteOn(49, 110);  // Crash Cymbal
            channels[9].noteOn(35, 120);  // Bass Drum
            channels[9].noteOn(41, 100);  // Low Tom
            channels[9].noteOn(55, 127);  // Splash Cymbal
        
            // Decay alami
            Thread.sleep(300);
        
            // Matikan semua nada
            channels[0].allNotesOff();
            channels[1].allNotesOff();
            channels[9].allNotesOff();
        
            // Reset controllers
            channels[0].controlChange(65, 0);     // Matikan portamento
            channels[0].setPitchBend(8192);       // Reset pitch bend

        } catch (Exception e) {
            System.out.println("Error playing fail sound: " + e.getMessage());
        }
    }
}