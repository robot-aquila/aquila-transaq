package ru.prolib.aquila.transaq.remote;

import ru.prolib.aquila.transaq.entity.SecType;

/**
 * Security identifier which points to
 * TRANSAQ security including its all significant data.
 */
public interface ISecIDF extends ISecIDG, ISecIDT {
	String getDefaultBoard();
	String getShortName();
	SecType getType();
}
