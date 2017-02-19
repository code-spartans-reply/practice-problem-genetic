package it.reply.open.challenge.ghc2017.model;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.Objects;

import org.jenetics.Chromosome;
import org.jenetics.IntegerGene;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import lombok.Setter;

public class TileChromosome implements Chromosome<IntegerGene> {

	private static final int END_Y_INDEX = 3;

	private static final int END_X_INDEX = 2;

	private static final int START_Y_INDEX = 1;

	private static final int START_X_INDEX = 0;

	private final ISeq<IntegerGene> genes;

	@Setter
	private int maxX = Integer.MAX_VALUE;

	@Setter
	private int maxY = Integer.MAX_VALUE;

	@Setter
	private int maxTile = Integer.MAX_VALUE;

	private TileChromosome(ISeq<IntegerGene> genes) {
		requireNonNull(genes, "Gene array");
		assert genes.forAll(Objects::nonNull) : "Found at least on null gene.";

		if (genes.isEmpty()) {
			throw new IllegalArgumentException("The genes sequence must contain at least one gene.");
		}

		if (genes.length() != 4) {
			throw new IllegalArgumentException("A tile must contain exactly four genes");
		}

		this.genes = genes;
	}

	@Override
	public Chromosome<IntegerGene> newInstance(ISeq<IntegerGene> genes) {
		TileChromosome tile = new TileChromosome(TileChromosome.shrinkToMax(genes, this.maxTile));
		tile.setMaxTile(this.maxTile);
		tile.setMaxX(this.maxX);
		tile.setMaxY(this.maxY);

		return tile;
	}

	@Override
	public IntegerGene getGene(int index) {
		return this.genes.get(index);
	}

	@Override
	public int length() {
		return 4;
	}

	@Override
	public ISeq<IntegerGene> toSeq() {
		return this.genes;
	}

	@Override
	public boolean isValid() {
		int startX = genes.get(START_X_INDEX).intValue();
		int startY = genes.get(START_Y_INDEX).intValue();
		int endX = genes.get(END_X_INDEX).intValue();
		int endY = genes.get(3).intValue();

		if (startX > endX || startY > endY) {
			return false;
		}

		if (this.getDimenstion() > maxTile) {
			return false;

		}

		return true;
	}

	@Override
	public Iterator<IntegerGene> iterator() {
		return this.genes.iterator();
	}

	@Override
	public Chromosome<IntegerGene> newInstance() {
		final TileChromosome tile = generateTile(this.maxX, this.maxY, this.maxTile);
		// System.out.println("Tile generated: (" + tile.getStartX() + ", " +
		// tile.getStartY() + "), (" + tile.getEndX() + ", " + tile.getEndY() +
		// ")");

		return tile;
	}

	public static TileChromosome of(int maxX, int maxY, int maxTile) {
		TileChromosome tile = generateTile(maxX, maxY, maxTile);

		return tile;
	}

	private static TileChromosome generateTile(int maxX, int maxY, int maxTile) {
		IntegerGene tileStartX = IntegerGene.of(0, maxX);
		IntegerGene tileStartY = IntegerGene.of(0, maxY);
		IntegerGene tileEndX = IntegerGene.of(tileStartX.intValue(), maxX);
		IntegerGene tileEndY = IntegerGene.of(tileStartY.intValue(), maxY);

		TileChromosome tile = new TileChromosome(TileChromosome.shrinkToMax(ISeq.of(tileStartX, tileStartY, tileEndX, tileEndY), maxTile));
		tile.setMaxTile(maxTile);
		tile.setMaxX(maxX);
		tile.setMaxY(maxY);

		return tile;
	}

	private static ISeq<IntegerGene> shrinkToMax(ISeq<IntegerGene> original, int max) {
		int tileStartX = original.get(START_X_INDEX).intValue();
		int tileStartY = original.get(START_Y_INDEX).intValue();
		int tileEndX = original.get(END_X_INDEX).intValue();
		int tileEndY = original.get(END_Y_INDEX).intValue();
		
		while (measureDimensions(tileStartX, tileStartY, tileEndX, tileEndY) > max) {
			boolean shrinkX = RandomRegistry.getRandom().nextBoolean();
			boolean moveStart = RandomRegistry.getRandom().nextBoolean();
			if (shrinkX) {
				if (moveStart) {
					++tileStartX;
				} else { 
					--tileEndX;
					}
			} else {
				if (moveStart) {
					++tileStartY;
				} else {
					--tileEndY ;
				}
			}
		}

		final IntegerGene finalStartX = IntegerGene.of(tileStartX, original.get(START_X_INDEX).getMin(), original.get(START_X_INDEX).getMax());
		final IntegerGene finalStartY = IntegerGene.of(tileStartY, original.get(START_Y_INDEX).getMin(), original.get(START_Y_INDEX).getMax());
		final IntegerGene finalEndX = IntegerGene.of(tileEndX, original.get(END_X_INDEX).getMin(), original.get(END_X_INDEX).getMax());
		final IntegerGene finalEndY = IntegerGene.of(tileEndY, original.get(END_Y_INDEX).getMin(), original.get(END_Y_INDEX).getMax());

		return ISeq.of(finalStartX, finalStartY, finalEndX, finalEndY);
	}

	private static int measureDimensions(int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
		return (tileEndX - tileStartX + 1) * (tileEndY - tileStartY + 1);
	}

	public int getStartX() {
		return this.genes.get(START_X_INDEX).intValue();
	}

	public int getStartY() {
		return this.genes.get(START_Y_INDEX).intValue();
	}

	public int getEndX() {
		return this.genes.get(END_X_INDEX).intValue();
	}

	public int getEndY() {
		return this.genes.get(END_Y_INDEX).intValue();
	}

	public int getDimenstion() {
		return (this.getEndX() - this.getStartX() + 1) * (this.getEndY() - this.getStartY() + 1);
	}
}
