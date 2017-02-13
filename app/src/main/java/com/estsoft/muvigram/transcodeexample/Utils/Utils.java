package com.estsoft.muvigram.transcodeexample.Utils;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by estsoft on 2017-02-13.
 */

public class Utils {

    public static String uriParseToAbsolutePath( Context context, Uri uri ) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );

        int id = Integer.parseInt(DocumentsContract.getDocumentId( uri ).split(":")[1]);
        int pathIndex = cursor.getColumnIndex( MediaStore.Images.Media.DATA );
        int idIndex = cursor.getColumnIndex( MediaStore.Images.Media._ID );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if ( cursor.getInt( idIndex ) == id ) return cursor.getString( pathIndex );
            } while( cursor.moveToNext() );
        }

        return null;
    }

    public static Long getVideoDuration( String filePath ) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        return Long.parseLong(retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DURATION ));

    }

}
