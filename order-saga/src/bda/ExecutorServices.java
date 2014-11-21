package bda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServices {

	private ExecutorService workStealingExecutorService;

	public ExecutorServices() {
		workStealingExecutorService = Executors.newWorkStealingPool();
	}

	public ExecutorService orderSaga() {
		return workStealingExecutorService;
	}

	public ExecutorService order() {
		return workStealingExecutorService;
	}

	public ExecutorService stock() {
		return workStealingExecutorService;
	}

	public ExecutorService payment() {
		return workStealingExecutorService;
	}

	public ExecutorService relationship() {
		return workStealingExecutorService;
	}

}
