package com.nvdai.changesound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class StartView extends View {
	private Context context;
	private Bitmap background;
	private Bitmap bitmap;

	public StartView(Context context) {
		super(context);
		this.context = context;
		bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.background);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		background = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(), false);
		canvas.drawBitmap(background, 0, 0,null);
	}
	
	

}
