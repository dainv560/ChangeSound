package com.nvdai.music;

import java.util.ArrayList;
import java.util.List;

import com.nvdai.database.DatabaseHandler;
import com.nvdai.database.SongData;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SongsManager {

	private ArrayList<SongDetails> songsList = new ArrayList<SongDetails>();
	SharedPreferences data;

	public SongsManager() {

	}

	public ArrayList<SongDetails> getPlaylist(Context context,String type) {
		DatabaseHandler db = new DatabaseHandler(context);
		List<SongData> listSongs = db.Order(type);
		for (SongData sd : listSongs) {
			SongDetails song = new SongDetails();
			song.setSongID(sd.getID());
			song.setSongName(sd.getName());
			song.setSongPath(sd.getPath());
			song.setSongArtist(sd.getArtist());
			song.setSongAlbum(sd.getAlbum());
			byte[] data = sd.getImage();
			if (data != null) {
				Bitmap image = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				song.setImage(image);
			} else {
				song.setImage(null);
			}

			songsList.add(song);
		}
		return songsList;
	}
	
	public ArrayList<SongDetails> orderPlaylist(Context context, String type) {
		DatabaseHandler db = new DatabaseHandler(context);
		List<SongData> listSongs = db.Order(type);
		for (SongData sd : listSongs) {
			SongDetails song = new SongDetails();
			song.setSongID(sd.getID());
			song.setSongName(sd.getName());
			song.setSongPath(sd.getPath());
			song.setSongArtist(sd.getArtist());
			song.setSongAlbum(sd.getAlbum());
			byte[] data = sd.getImage();
			if (data != null) {
				Bitmap image = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				song.setImage(image);
			} else {
				song.setImage(null);
			}

			songsList.add(song);
		}
		return songsList;
	} 
}
