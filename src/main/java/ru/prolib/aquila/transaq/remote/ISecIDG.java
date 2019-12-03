package ru.prolib.aquila.transaq.remote;

/**
 * Interface of security identifier which points to
 * TRANSAQ security generally (without board). 
 */
public interface ISecIDG {
	String getSecCode();
	int getMarketID();
}
