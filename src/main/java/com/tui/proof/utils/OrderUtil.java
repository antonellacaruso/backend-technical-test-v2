package com.tui.proof.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrderUtil {

	public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
		return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}
}
