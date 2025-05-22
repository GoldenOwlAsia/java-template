package com.goldenowl.springboottemplate.email.service;

import com.goldenowl.springboottemplate.email.dto.CompleteUserMailDTO;
import com.goldenowl.springboottemplate.email.dto.VerifyUserMailDTO;

public interface MailService {

    void sendVerifyUserMail(VerifyUserMailDTO verifyUserMailDTO);
    void sendCompleteUserMail(CompleteUserMailDTO completeUserMailDTO);
}
