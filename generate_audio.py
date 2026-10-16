import math
import struct
import wave
import os

SAMPLE_RATE = 22050

def write_wav(filename, samples):
    with wave.open(filename, 'w') as wav:
        wav.setnchannels(1)  # Mono
        wav.setsampwidth(2)  # 16-bit
        wav.setframerate(SAMPLE_RATE)
        # Normalize and clamp samples
        max_val = max(abs(s) for s in samples) if samples else 1.0
        if max_val < 1e-6:
            max_val = 1.0
        scale = 32000.0 / max(1.0, max_val)
        
        packed = bytearray()
        for s in samples:
            val = int(s * scale)
            val = max(-32767, min(32767, val))
            packed.extend(struct.pack('<h', val))
        wav.writeframes(packed)
    print(f"Generated {filename} ({len(samples)/SAMPLE_RATE:.2f}s)")

def make_note(freq, duration_sec, sample_rate=SAMPLE_RATE, wave_type='sine'):
    n_samples = int(duration_sec * sample_rate)
    samples = []
    for i in range(n_samples):
        t = i / sample_rate
        # Envelope: quick attack, exponential release
        env = min(1.0, i / (0.01 * sample_rate)) * math.exp(-3.0 * t / duration_sec)
        if wave_type == 'sine':
            val = math.sin(2 * math.pi * freq * t)
        elif wave_type == 'bell':
            val = 0.6 * math.sin(2 * math.pi * freq * t) + 0.3 * math.sin(2 * math.pi * freq * 2 * t) + 0.1 * math.sin(2 * math.pi * freq * 3 * t)
        elif wave_type == 'chime':
            val = 0.5 * math.sin(2 * math.pi * freq * t) + 0.3 * math.sin(2 * math.pi * freq * 2.756 * t) + 0.2 * math.sin(2 * math.pi * freq * 5.404 * t)
        else:
            val = math.sin(2 * math.pi * freq * t)
        samples.append(val * env)
    return samples

def generate_sfx_click(filepath):
    # Short snappy click
    n_samples = int(0.04 * SAMPLE_RATE)
    samples = []
    for i in range(n_samples):
        t = i / SAMPLE_RATE
        freq = 1200 - (t / 0.04) * 800
        env = math.exp(-40.0 * t)
        val = math.sin(2 * math.pi * freq * t) * env
        samples.append(val * 0.7)
    write_wav(filepath, samples)

def generate_sfx_correct(filepath):
    # Cheerful ascending major chime: C5 (523), E5 (659), G5 (784), C6 (1046)
    notes = [523.25, 659.25, 783.99, 1046.50]
    note_dur = 0.08
    total_dur = 0.45
    total_samples = int(total_dur * SAMPLE_RATE)
    samples = [0.0] * total_samples
    
    for idx, f in enumerate(notes):
        start_idx = int(idx * note_dur * SAMPLE_RATE)
        dur = 0.25 if idx == len(notes)-1 else 0.18
        note_samples = make_note(f, dur, wave_type='chime')
        for i, s in enumerate(note_samples):
            if start_idx + i < total_samples:
                samples[start_idx + i] += s * 0.6
    write_wav(filepath, samples)

def generate_sfx_wrong(filepath):
    # Gentle error buzz: 2 descending chord steps: Eb4 (311) -> C4 (261)
    dur = 0.35
    total_samples = int(dur * SAMPLE_RATE)
    samples = []
    for i in range(total_samples):
        t = i / SAMPLE_RATE
        f = 311.13 - (t / dur) * 80.0
        env = min(1.0, i / (0.01 * SAMPLE_RATE)) * (1.0 - t / dur)
        # Add slight square harmonics for buzzer feel
        val = 0.6 * math.sin(2 * math.pi * f * t) + 0.3 * math.sin(2 * math.pi * 2 * f * t) + 0.1 * math.sin(2 * math.pi * 3 * f * t)
        samples.append(val * env * 0.7)
    write_wav(filepath, samples)

def generate_sfx_start(filepath):
    # Cheerful game start whoosh / chime: G4 -> C5 -> E5 -> G5
    notes = [392.0, 523.25, 659.25, 783.99]
    note_dur = 0.09
    total_dur = 0.55
    total_samples = int(total_dur * SAMPLE_RATE)
    samples = [0.0] * total_samples
    for idx, f in enumerate(notes):
        start_idx = int(idx * note_dur * SAMPLE_RATE)
        dur = 0.3
        note_samples = make_note(f, dur, wave_type='bell')
        for i, s in enumerate(note_samples):
            if start_idx + i < total_samples:
                samples[start_idx + i] += s * 0.6
    write_wav(filepath, samples)

def generate_sfx_victory(filepath):
    # Celebratory victory fanfare: C5, E5, G5, high C6 chord
    chords = [
        ([523.25], 0.12),
        ([659.25], 0.12),
        ([783.99], 0.14),
        ([523.25, 659.25, 1046.50], 0.45)
    ]
    total_dur = 0.9
    total_samples = int(total_dur * SAMPLE_RATE)
    samples = [0.0] * total_samples
    cur_t = 0.0
    for freqs, dur in chords:
        start_idx = int(cur_t * SAMPLE_RATE)
        for f in freqs:
            note_samples = make_note(f, dur, wave_type='chime')
            for i, s in enumerate(note_samples):
                if start_idx + i < total_samples:
                    samples[start_idx + i] += s * (0.5 / len(freqs))
        cur_t += dur * 0.75
    write_wav(filepath, samples)

def generate_bgm_game(filepath):
    # A bright, cheerful, educational game instrumental loop (120 BPM, ~8.0 seconds)
    # Chord progression: C - G - Am - F (4 bars, 2 beats each bar = 8 beats = 8 / (120/60) = 4.0s)
    # 2 loops = 8.0 seconds total, perfectly seamless.
    bpm = 124.0
    beat_sec = 60.0 / bpm
    bar_sec = beat_sec * 2.0  # 2 beats per chord
    
    progression = [
        # (Bass freq, Chord freqs, Melody notes [offset_beats, freq, dur])
        (130.81, [261.63, 329.63, 392.00], [(0.0, 523.25, 0.4), (0.5, 659.25, 0.3), (1.0, 783.99, 0.5), (1.5, 659.25, 0.3)]), # C
        (98.00,  [196.00, 246.94, 293.66], [(0.0, 587.33, 0.4), (0.5, 493.88, 0.3), (1.0, 587.33, 0.5), (1.5, 783.99, 0.3)]), # G
        (110.00, [220.00, 261.63, 329.63], [(0.0, 659.25, 0.4), (0.5, 523.25, 0.3), (1.0, 659.25, 0.5), (1.5, 880.00, 0.3)]), # Am
        (87.31,  [174.61, 220.00, 261.63], [(0.0, 698.46, 0.4), (0.5, 659.25, 0.3), (1.0, 587.33, 0.4), (1.5, 523.25, 0.5)])  # F
    ]
    
    total_beats = 16 # 8 bars total = 8.0 seconds
    total_dur = total_beats * beat_sec
    total_samples = int(total_dur * SAMPLE_RATE)
    samples = [0.0] * total_samples
    
    # Render 2 cycles of 4 chords
    for cycle in range(2):
        for chord_idx, (bass_f, chord_fs, melody) in enumerate(progression):
            chord_start_t = (cycle * 4 + chord_idx) * bar_sec
            
            # Bass note (electric piano synth bass)
            bass_samples = make_note(bass_f, bar_sec * 0.9, wave_type='sine')
            start_i = int(chord_start_t * SAMPLE_RATE)
            for i, s in enumerate(bass_samples):
                if start_i + i < total_samples:
                    samples[start_i + i] += s * 0.35
            
            # Soft pad chord arpeggio
            for b in [0.0, 1.0]:
                pad_t = chord_start_t + b * beat_sec
                pad_i = int(pad_t * SAMPLE_RATE)
                for cf in chord_fs:
                    pad_s = make_note(cf, beat_sec * 0.8, wave_type='bell')
                    for i, s in enumerate(pad_s):
                        if pad_i + i < total_samples:
                            samples[pad_i + i] += s * 0.12
            
            # Cheerful marimba / music-box melody
            for offset_b, mel_f, mel_dur in melody:
                note_t = chord_start_t + offset_b * beat_sec
                note_i = int(note_t * SAMPLE_RATE)
                mel_s = make_note(mel_f, mel_dur * beat_sec, wave_type='chime')
                for i, s in enumerate(mel_s):
                    if note_i + i < total_samples:
                        samples[note_i + i] += s * 0.35

    write_wav(filepath, samples)

if __name__ == '__main__':
    raw_dir = 'app/src/main/res/raw'
    os.makedirs(raw_dir, exist_ok=True)
    generate_sfx_click(os.path.join(raw_dir, 'sfx_click.wav'))
    generate_sfx_correct(os.path.join(raw_dir, 'sfx_correct.wav'))
    generate_sfx_wrong(os.path.join(raw_dir, 'sfx_wrong.wav'))
    generate_sfx_start(os.path.join(raw_dir, 'sfx_start.wav'))
    generate_sfx_victory(os.path.join(raw_dir, 'sfx_victory.wav'))
    generate_bgm_game(os.path.join(raw_dir, 'bgm_game.wav'))
    print("All audio files successfully generated!")
