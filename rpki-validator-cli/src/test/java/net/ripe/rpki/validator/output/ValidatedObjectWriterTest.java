/**
 * The BSD License
 *
 * Copyright (c) 2010-2012 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.rpki.validator.output;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.ripe.rpki.commons.crypto.x509cert.X509ResourceCertificate;
import net.ripe.rpki.commons.crypto.x509cert.X509ResourceCertificateTest;
import net.ripe.rpki.commons.validation.ValidationResult;
import net.ripe.rpki.validator.util.UriToFileMapper;
import net.ripe.rpki.validator.util.UriToFileMapperTest;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;


public class ValidatedObjectWriterTest {

    private static final File TEST_TARGET_DIRECTORY = new File(System.getProperty("java.io.tmp", "/tmp"));

    private static final URI TEST_OBJECT_URI = URI.create("RSYNC://localhost:9999/repo/ca%20repo/object.cer");

    private static final File TEST_OBJECT_FILE = new File(TEST_TARGET_DIRECTORY, "localhost:9999/repo/ca%20repo/object.cer");

    private ValidationResult result;
    private X509ResourceCertificate certificate;
    private ValidatedObjectWriter subject;

    @Before
    public void setUp() {
        result = ValidationResult.withLocation(TEST_OBJECT_URI);

        certificate = X509ResourceCertificateTest.createSelfSignedCaResourceCertificate();
        subject = new ValidatedObjectWriter(new UriToFileMapper(TEST_TARGET_DIRECTORY));
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(TEST_OBJECT_FILE.getParentFile());
    }

    @Test
    public void shouldCopyValidatedObjectToTargetDirectory() throws IOException {
        subject.afterFetchSuccess(TEST_OBJECT_URI, certificate, result);

        assertTrue("file created", TEST_OBJECT_FILE.exists());
        assertArrayEquals("contents match", certificate.getEncoded(), FileUtils.readFileToByteArray(TEST_OBJECT_FILE));
    }

    @Test
    public void shouldNotOverwriteExistingFile() throws IOException {
        FileUtils.writeStringToFile(TEST_OBJECT_FILE, "123");
        subject.afterFetchSuccess(TEST_OBJECT_URI, certificate, result);

        assertTrue("file created", TEST_OBJECT_FILE.exists());
        assertEquals("123", Files.toString(TEST_OBJECT_FILE, Charsets.UTF_8));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldFailIfUriCannotBeMappedToFile() {
        subject.afterFetchSuccess(UriToFileMapperTest.INVALID_URI, certificate, result);
    }

}
