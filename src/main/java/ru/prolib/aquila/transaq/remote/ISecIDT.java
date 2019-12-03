package ru.prolib.aquila.transaq.remote;

/**
 * Security identifier which points to
 * TRANSAQ security on specified trading board.
 */
public interface ISecIDT {
	String getSecCode();
	String getBoardCode();
}
