package in.omdev.vaccinenotif;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import in.omdev.vaccinenotif.entity.Centers;

public class Util {
    public static String getSessionInfo(Centers.Center center, Centers.Session session) {
        return center.getName() + " (" + center.getFee_type() + ")\n" +
                center.getAddress() + "\n" +
                center.getDistrict_name() + ", " + center.getState_name() + ", "
                + center.getPincode() + "\n" +
                "\n\n" +
                "Date: " + session.getDate() + "\n" +
                "Doses available: " + session.getAvailable_capacity() + "\n" +
                "Vaccine: " + session.getVaccine() + "\n" +
                "Age: " + session.getMin_age_limit() + "+";
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
