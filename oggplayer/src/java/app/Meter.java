package app;

public class Meter
{
	private int numerator, denominator;
	private double tempo_bpm;
	
	public Meter(int numerator, int denominator, double tempo_bpm)
	{
		this.numerator = numerator;
		this.denominator = denominator;
		this.tempo_bpm = tempo_bpm;
	}
	
	public double timeSpanToBeats(double timeSpan_sec)
	{
		double beat_length_sec = (1.0 / Meter.this.tempo_bpm) * 60.0;
		return timeSpan_sec / beat_length_sec;
	}
	
	public double beatsToTimeSpan(double beats)
	{
		double beat_length_sec = (1.0 / Meter.this.tempo_bpm) * 60.0;
		return beats * beat_length_sec;
	}
	
	public int getNumerator()
	{
		return numerator;
	}
	
	public class Position
	{
		/**
		 * Full bars number. 
		 * Counted from 1.
		 */
		int bar;
		
		/**
		 * Beats. Contains halfs, quarters, etc. 
		 * Counted from 1.
		 */
		double beat;
		
		public Position(int bar, double beat)
		{
			this.bar = bar;
			this.beat = beat;
		}
		
		/**
		 * Creates the position from time in seconds
		 * @param time_sec time in seconds
		 */
		public Position(double time_sec)
		{
			double beat_length_sec = (1.0 / Meter.this.tempo_bpm) * 60.0;

			int full_beats = (int)(time_sec / beat_length_sec);
			int full_bars = full_beats / Meter.this.numerator;
			
			int full_beats_in_last_bar = full_beats % Meter.this.numerator;
			
			double remaining_time_sec = time_sec - beat_length_sec * full_beats;
			double remaining_beats = remaining_time_sec / beat_length_sec;
			
			this.bar = full_bars + 1;
			this.beat = full_beats_in_last_bar + remaining_beats + 1;
		}
		
		public double toTime()
		{
			double beat_length_sec = (1.0 / Meter.this.tempo_bpm) * 60.0;
			
			return beat_length_sec * ((bar - 1) * Meter.this.numerator + (beat - 1));
		}
		
		public double getBeat()
		{
			return beat;
		}
		
		public int getBar()
		{
			return bar;
		}
		
		@Override
		public String toString()
		{
			return bar + " : " + beat;
		}
	}
}
