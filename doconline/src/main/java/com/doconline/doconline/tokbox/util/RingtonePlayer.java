package com.doconline.doconline.tokbox.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.doconline.doconline.R;

/**
 * Helper class to play a ringtone and vibrate.
 */
public class RingtonePlayer
{
    private static final String TAG = RingtonePlayer.class.getSimpleName();
    private MediaPlayer mediaPlayer;

    /**
     * Constructor which instantiates and initialises the {@link RingtonePlayer} object with the
     * given context.
     *
     * @param context the context which is used to lookup the default ringtone resource file.
     */
    public RingtonePlayer(final Context context)
    {
        mediaPlayer = new MediaPlayer();
        final Uri ringtoneUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);

        try
        {
            mediaPlayer.setDataSource(context, ringtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.prepare();
        }

        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Starts the ringtone playback and vibration.
     *
     * @param looping should the ringtone keep repeating or play once and stop.
     */
    public void play(final boolean looping)
    {
        Log.i(TAG, "play");

        if (mediaPlayer == null)
        {
            Log.i(TAG, "mediaPlayer isn't created ");
            return;
        }

        mediaPlayer.setLooping(looping);
        mediaPlayer.start();
    }

    /**
     * Stops the ringtone playback and vibration.
     */
    public synchronized void stop()
    {
        if (mediaPlayer != null)
        {
            try
            {
                mediaPlayer.stop();
            }

            catch (final IllegalStateException e)
            {
                e.printStackTrace();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}