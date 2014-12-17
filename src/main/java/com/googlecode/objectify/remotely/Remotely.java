package com.googlecode.objectify.remotely;


import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.util.concurrent.Callable;

/**
 *
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class Remotely
{
	private static final ThreadLocal<Boolean> ENABLED = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	/** */
	private static RemoteApiOptions options;

	/** */
	public static void setOptions(RemoteApiOptions value) {
		options = value;
	}

	/** */
	public static RemoteApiOptions getOptions() {
		return options;
	}

	/** @return true if we should use the remote api right now */
	public static boolean isEnabled() {
		return ENABLED.get();
	}

	/**
	 * Execute the work against a remote datastore.
	 */
	public static <R> R execute(Callable<R> work) {
		if (options == null)
			throw new IllegalStateException("You must set options first");

		boolean prior = ENABLED.get();
		ENABLED.set(true);

		try {
			return work.call();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ENABLED.set(prior);
		}
	}
}
