package com.golfpvcc.teamscore.Extras;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailScores {
    private String[] m_To = {"vgamble@golfpvcc.com"};
    private String m_Subject = "Team Score";
    private String m_Body = "my scores";
    private Context m_Context;

    public EmailScores(Context context) {
        m_Context = context;
    }

    public void ToPostOffice() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, m_To);
        intent.putExtra(Intent.EXTRA_SUBJECT, m_Subject);
        intent.putExtra(Intent.EXTRA_TEXT, m_Body);
        intent.setType("message/rfc822");

        Intent chooser = Intent.createChooser(intent, "Send Email");
        m_Context.startActivity(chooser);
    }

    public void SetEmailAddress(String EmailAddress) {
        m_To[0] = EmailAddress;
    }

    public void SetEmailSubject(String Subject) {
        m_Subject = Subject;
    }

    public void SetEmailBody(String Body) {
        m_Body = Body;
    }
}
