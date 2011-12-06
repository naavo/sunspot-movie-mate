package movie.mate.player;

import quicktime.*;
import quicktime.app.view.*;
import quicktime.std.movies.*;
import quicktime.io.*;
import sun.net.httpserver.HttpServerImpl;

//import com.oreilly.qtjnotebook.ch01.QTSessionCheck;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public class BasicQTPlayer extends Frame {
	public BasicQTPlayer(Movie m) throws QTException {
		super("Basic QT Player");
		QTComponent qc = QTFactory.makeQTComponent(m);
		Component c = qc.asComponent();
		add(c);
	}

	public static void main(String[] args) {
		try {
			QTFile file = QTFile
					.standardGetFilePreview(QTFile.kStandardQTFileTypes);
			OpenMovieFile omFile = OpenMovieFile.asRead(file);
			Movie m = Movie.fromFile(omFile);
			Frame f = new BasicQTPlayer(m);
			f.pack();
			f.setVisible(true);
			m.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}