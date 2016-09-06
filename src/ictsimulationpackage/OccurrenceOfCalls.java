package ictsimulationpackage;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class OccurrenceOfCalls {
	static int Occurrence(double lambda) {
		int k = 0;
		if (lambda < 600) {
			k = poisson(lambda);
		} else {
			int l[] = new int[60];
			lambda = lambda / 60;
			for (int i = 0; i < 60; i++) {
				l[i] = poisson(lambda);
				k += l[i];
			}
		}
		return k;
	}

	static int poisson(double lambda) {
		double xp;
		int k = 0;
		int l[] = new int[60];
		xp = Math.random();
		while (xp >= Math.exp(-lambda)) {
			xp = xp * Math.random();
			k = k + 1;
		}
		return (k);
	}

}
