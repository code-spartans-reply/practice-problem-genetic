package it.reply.open.challenge.ghc2017.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProblemParameters {

	private final int minIngredientsNumber;
	
	private final int maxSliceDimension;
	
	private final char[][] pizza;
	
	public int getPizzaWidth() {
		return this.pizza.length;
	}
	
	public int getPizzaHeight() {
		return this.pizza[0].length;
	}
	
}
