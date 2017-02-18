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
			throw new IllegalArgumentException(
				"The genes sequence must contain at least one gene."
			);
		}

		if (genes.length() != 4) {
			throw new IllegalArgumentException("A tile must contain exactly four genes");
		}
		
		this.genes = genes;
	}
	
	@Override
	public Chromosome<IntegerGene> newInstance(ISeq<IntegerGene> genes) {
		return new TileChromosome(genes);
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
		int startX = genes.get(0).intValue();
		int startY = genes.get(1).intValue();
		int endX = genes.get(2).intValue();
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
//		System.out.println("Tile generated: (" + tile.getStartX() + ", " + tile.getStartY() + "), (" + tile.getEndX() + ", " + tile.getEndY() + ")");
		
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
		
		while ((tileEndX.intValue() - tileStartX.intValue() + 1) * (tileEndY.intValue() - tileStartY.intValue() + 1) > maxTile) {
			boolean shrinkX = RandomRegistry.getRandom().nextBoolean();
			if (shrinkX) {
				tileEndX = IntegerGene.of(tileEndX.intValue() - 1, tileStartX.intValue(), maxX);
			} else {
				tileEndY = IntegerGene.of(tileEndY.intValue() - 1, tileStartY.intValue(), maxY);
			}
		}
		
		TileChromosome tile = new TileChromosome(ISeq.of(tileStartX, tileStartY, tileEndX, tileEndY));
		tile.setMaxTile(maxTile);
		tile.setMaxX(maxX);
		tile.setMaxY(maxY);
		
		return tile;
	}

	public int getStartX() {
		return this.genes.get(0).intValue();
	}

	public int getStartY() {
		return this.genes.get(1).intValue();
	}

	public int getEndX() {
		return this.genes.get(2).intValue();
	}
	
	public int getEndY() {
		return this.genes.get(3).intValue();
	}

	public int getDimenstion() {
		return (this.getEndX() - this.getStartX() + 1) * (this.getEndY() - this.getStartY() + 1);
	}
}
