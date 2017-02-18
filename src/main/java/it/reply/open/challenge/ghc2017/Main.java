package it.reply.open.challenge.ghc2017;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Scanner;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import com.google.common.base.Preconditions;

import it.reply.open.challenge.ghc2017.model.ProblemParameters;
import it.reply.open.challenge.ghc2017.model.TileChromosome;

public class Main {

	private static class RandomGenotypeFactory<G extends Gene<?, G>> implements Factory<Genotype<G>> {

		private final Factory<Chromosome<G>> factory;

		public RandomGenotypeFactory(Factory<Chromosome<G>> factory) {
			this.factory = factory;
		}

		@Override
		public Genotype<G> newInstance() {
			final ISeq<Chromosome<G>> ch = ISeq.of(factory::newInstance,
					Math.max(1, Math.abs(RandomRegistry.getRandom().nextInt(100))));
			return Genotype.of(ch);
		}

	}

	public static void main(String[] args) throws Exception {
		final ProblemParameters parameters = Main.readInputParametersFrom(args[0]);

		RandomGenotypeFactory<IntegerGene> randomGenotypeFactory = new RandomGenotypeFactory<>(TileChromosome.of(
				parameters.getPizzaWidth() - 1, parameters.getPizzaHeight() - 1, parameters.getMaxSliceDimension()));

		Genotype<IntegerGene> result = Engine.builder((genotype) -> {
			return evaluatePizzaSlicing(genotype, parameters);
		}, randomGenotypeFactory).build().stream().limit(300).collect(EvolutionResult.toBestGenotype());

		Main.renderOutput(result, parameters);
	}

	private static void renderOutput(Genotype<IntegerGene> result, ProblemParameters parameters) {
		final String[][] outputMatrix = new String[parameters.getPizzaWidth()][parameters.getPizzaHeight()];

		int chromosomeId = 0;
		for (final Chromosome<IntegerGene> chromosome : result) {
			final TileChromosome tile = (TileChromosome) chromosome;
			System.out.println(String.format("Chromosome %d: (%d, %d) (%d, %d)", chromosomeId, tile.getStartX(),
					tile.getStartY(), tile.getEndX(), tile.getEndY()));
			for (int currentX = tile.getStartX(); currentX <= tile.getEndX(); ++currentX) {
				for (int currentY = tile.getStartY(); currentY <= tile.getEndY(); ++currentY) {
					outputMatrix[currentX][currentY] = String.format("|%10s|",
							chromosomeId + " (" + parameters.getPizza()[currentX][currentY] + ")");
				}
			}
			++chromosomeId;
		}

		for (int i = 0; i < parameters.getPizzaWidth(); ++i) {
			for (int j = 0; j < parameters.getPizzaHeight(); ++j) {

				System.out.print(outputMatrix[i][j]);
			}
			System.out.println();
		}

	}

	private static double evaluatePizzaSlicing(Genotype<IntegerGene> genotype, ProblemParameters parameters) {
		final double[][] evaluationMatrix = new double[parameters.getPizzaWidth()][parameters.getPizzaHeight()];
		int chromoId = -1;
		for (final Chromosome<IntegerGene> chromosome : genotype) {
			++chromoId;
			final TileChromosome tile = (TileChromosome) chromosome;
			int tomatoes = 0;
			int mushrooms = 0;
			for (int currentX = tile.getStartX(); currentX <= tile.getEndX(); ++currentX) {
				for (int currentY = tile.getStartY(); currentY <= tile.getEndY(); ++currentY) {
					if (parameters.getPizza()[currentX][currentY] == 'T') {
						++tomatoes;
					} else {
						++mushrooms;
					}
				}
			}

			double basePoints = tomatoes >= parameters.getMinIngredientsNumber()
					&& mushrooms >= parameters.getMinIngredientsNumber() ? 10d : 0d;

			System.out.println(String.format("Chromosome %d is %d cells wide and has %d tomatoes and %d mushrooms: basePoints = %1.2f", chromoId, tile.getDimenstion(), tomatoes, mushrooms, basePoints));

			for (int currentX = tile.getStartX(); currentX <= tile.getEndX(); ++currentX) {
				for (int currentY = tile.getStartY(); currentY <= tile.getEndY(); ++currentY) {
					double currentValue = evaluationMatrix[currentX][currentY];
					double newValue = currentValue > 0d ? 0.2d * currentValue : basePoints;

					evaluationMatrix[currentX][currentY] = newValue;
				}
			}
		}
		double fitness = 0d;
		for (int i = 0; i < parameters.getPizzaWidth(); ++i) {
			for (int j = 0; j < parameters.getPizzaHeight(); ++j) {
				fitness += evaluationMatrix[i][j];
			}
		}
		
		System.out.println(String.format("Genotype fitness: %10.2f", fitness));
		return fitness;
	}

	private static ProblemParameters readInputParametersFrom(String filename)
			throws FileNotFoundException, IOException {
		Preconditions.checkArgument(Preconditions.checkNotNull(filename).trim().isEmpty() == false,
				"You must supply an input file name");

		ProblemParameters problemParameters = null;
		try (final Scanner inputData = new Scanner(FileSystems.getDefault().getPath(filename), "UTF-8")) {
			;
			inputData.useDelimiter("\\s");

			final char[][] pizza = new char[inputData.nextInt()][inputData.nextInt()];
			System.out.println("Pizza size: " + pizza.length + ", " + pizza[0].length);
			final int minIngredientsNumber = inputData.nextInt();
			System.out.println("Ingradients per slice: " + minIngredientsNumber);
			final int maxSliceDimension = inputData.nextInt();
			System.out.println("Max slice: " + maxSliceDimension);

			int i = 0;
			while (inputData.hasNextLine()) {
				String pizzaLayout = inputData.nextLine();
				if (pizzaLayout.isEmpty() == false) {
					pizza[i++] = pizzaLayout.toCharArray();
				}
			}
			problemParameters = new ProblemParameters(minIngredientsNumber, maxSliceDimension, pizza);
		}
		return problemParameters;
	}

}
