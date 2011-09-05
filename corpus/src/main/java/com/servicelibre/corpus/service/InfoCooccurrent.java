package com.servicelibre.corpus.service;

public class InfoCooccurrent implements Comparable<InfoCooccurrent> {
	
	public String terme;
	public String cooccurrent;
	public int freqAvant;
	public int freqAprès;
	public int freq;
	public int distance;
	@Override
	public String toString() {
		return "InfoCooccurrent [terme=" + terme + ", cooccurrent="
				+ cooccurrent + ", freq_avant=" + freqAvant + ", freq_après="
				+ freqAprès + ", freq=" + freq + ", distance=" + distance
				+ "]";
	}
	
	
	// Trie par défaut par ordre décroissant de fréquence
//	public int compareTo(final InfoCooccurrent other) {
//		return new CompareToBuilder().append(other.freq,freq).toComparison();
//	}
	
	public int compareTo(final InfoCooccurrent other) {
		
		if(other == null) {
			return -1;
		}
		
		if (other.freq > this.freq) {
			return 1;
		}
		else if(other.freq == this.freq) {
			return 0;
		}
		else {
			return -1;
		}
	}


	public String getTerme() {
		return terme;
	}


	public void setTerme(String terme) {
		this.terme = terme;
	}


	public String getCooccurrent() {
		return cooccurrent;
	}


	public void setCooccurrent(String cooccurrent) {
		this.cooccurrent = cooccurrent;
	}


	public int getFreqAvant() {
		return freqAvant;
	}


	public void setFreqAvant(int freqAvant) {
		this.freqAvant = freqAvant;
	}


	public int getFreqAprès() {
		return freqAprès;
	}


	public void setFreqAprès(int freqAprès) {
		this.freqAprès = freqAprès;
	}


	public int getFreq() {
		return freq;
	}


	public void setFreq(int freq) {
		this.freq = freq;
	}


	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	

	
	
}
