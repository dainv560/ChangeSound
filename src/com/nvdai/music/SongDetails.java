package com.nvdai.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SongDetails {

	private Integer songID;
	private String songName;
	private String songPath;
	private String songArtist;
	private String songAlbum;
	private Bitmap image;
	private Integer favorite;

	public SongDetails() {

	}

	public SongDetails(Integer songID, String songName, String songPath,
			String songArtist, String songAlbum, byte[] data, Integer favorite) {
		this.songID = songID;
		this.songName = songName;
		this.songPath = songPath;

		if (data != null) {
			image = BitmapFactory.decodeByteArray(data, 0, data.length);
		} else {
			image = null;
		}

		this.songArtist = songArtist;
		this.songAlbum = songAlbum;
		this.favorite = favorite;
	}

	public Integer getFavorite() {
		return favorite;
	}

	public void setFavorite(Integer favorite) {
		this.favorite = favorite;
	}

	public Integer getSongID() {
		return songID;
	}

	public void setSongID(Integer songID) {
		this.songID = songID;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getSongArtist() {
		return songArtist;
	}

	public void setSongArtist(String songArtist) {
		this.songArtist = songArtist;
	}

	public String getSongAlbum() {
		return songAlbum;
	}

	public void setSongAlbum(String songAlbum) {
		this.songAlbum = songAlbum;
	}

	public String getSongPath() {
		return songPath;
	}

	public void setSongPath(String songPath) {
		this.songPath = songPath;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

}