package com.nvdai.music;

import java.util.ArrayList;
import java.util.List;

import com.nvdai.changesound.R;
import com.nvdai.database.DatabaseHandler;
import com.nvdai.database.SongData;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SongsLibrary extends ListActivity {

	private EditText etSearch;
	private TextView tvStatus;
	private ListView lv;
	private Songs adapter;
	private Spinner sortSpinner;
	private String[] typeSort = { "Name", "Artist", "Album", "Favorite",
			"Only Favorite" };
	private ArrayAdapter<String> sortAdapter;
	private ArrayList<SongDetails> songs = new ArrayList<SongDetails>();
	private Context context;
	private static int typeSelected = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.library_layout);
		context = this;
		DatabaseHandler db = new DatabaseHandler(context);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		tvStatus = (TextView) findViewById(R.id.tvStatus);
		etSearch = (EditText) findViewById(R.id.inputSearch);
		sortSpinner = (Spinner) findViewById(R.id.spSortBy);

		sortAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, typeSort);
		sortAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(sortAdapter);
		sortSpinner.setSelection(typeSelected);
		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				songs.clear();
				typeSelected = position;
				DatabaseHandler db = new DatabaseHandler(context);
				List<SongData> listSongs = db.Order(typeSort[position]
						.toLowerCase());
				loadList(listSongs);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});

		List<SongData> listSongs = db.getAllSongDatas();
		loadList(listSongs);
		lv.setItemsCanFocus(false);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int songIndex = getPosition(songs.get(position).getSongID(),
						typeSort[sortSpinner.getSelectedItemPosition()]
								.toLowerCase());

				Intent index = new Intent(getApplicationContext(),
						PlayMusic.class);
				index.putExtra("songIndex", songIndex);
				index.putExtra("typeSort", typeSort[sortSpinner
						.getSelectedItemPosition()].toLowerCase());
				setResult(100, index);

				finish();
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!adapter.isEmpty()){
					adapter.getFilter().filter(s);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	/*
	 * Method to load all song in Database to ListView with an adapter
	 */

	private void loadList(List<SongData> listSongs) {
		for (SongData sd : listSongs) {
			SongDetails song = new SongDetails();
			song.setSongID(sd.getID());
			song.setSongName(sd.getName());
			song.setSongPath(sd.getPath());
			song.setSongArtist(sd.getArtist());
			song.setSongAlbum(sd.getAlbum());
			byte[] data = sd.getImage();
			if (data != null) {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inPurgeable = true;
                
				Bitmap image = BitmapFactory.decodeByteArray(data, 0,
						data.length,options);
				if( image!=null){
					Bitmap songImage = Bitmap.createScaledBitmap(image, 100 , 100 , true);
					song.setImage(songImage);
				}else{
					song.setImage(null);
				}
			} else {
				song.setImage(null);
			}
			song.setFavorite(sd.getFavorite());

			songs.add(song);
		}
		if (songs.size() > 0) {
			adapter = new Songs(SongsLibrary.this, songs,
					typeSort[typeSelected].toLowerCase());
			tvStatus.setHeight(0);
		} else {
			tvStatus.setText("No Songs to show");
		}

		lv = getListView();
		lv.setAdapter(adapter);
	}

	/*
	 * Method to get position of one song in list base on song ID
	 */
	public int getPosition(int ID, String typeSort) {
		DatabaseHandler db = new DatabaseHandler(context);
		List<SongData> listSongs = db.Order(typeSort);
		db.close();
		int pos = 0;
		for (SongData sd : listSongs) {
			if (sd.getID() == ID) {
				break;
			}
			pos++;
		}
		return pos;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(SongsLibrary.this, PlayMusic.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
