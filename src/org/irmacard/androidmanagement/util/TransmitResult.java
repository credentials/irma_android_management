/**
 * TransmitResult.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, March` 2013.
 */

package org.irmacard.androidmanagement.util;

public class TransmitResult {
	public enum Result {
		SUCCESS, FAILURE, INCORRECT_PIN
	}

	private Result result;
	private Exception exception = null;

	public TransmitResult(Exception exception) {
		this.result = Result.FAILURE;
		this.exception = exception;
	}

	public TransmitResult(Result result) {
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	public Exception getException() {
		return exception;
	}
}