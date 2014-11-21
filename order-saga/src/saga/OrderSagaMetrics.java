package saga;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderSagaMetrics {

	private long start = -1;
	private AtomicInteger count = new AtomicInteger(0);

	public void beginOrder(String orderHash) {
		if (start < 0) {
			start = System.nanoTime();
		}
	}

	public void endOrder(String orderHash) {
		int c = count.incrementAndGet();
		long total = System.nanoTime() - start;
		long avg = total / c;
		System.out.println("Count: " + c + ", Avg: " + avg);
	}

}
