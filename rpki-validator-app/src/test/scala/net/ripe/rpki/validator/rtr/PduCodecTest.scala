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
package net.ripe.rpki.validator.rtr

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer
import org.scalatest.mock.MockitoSugar
import org.jboss.netty.channel._
import net.ripe.rpki.validator.support.ValidatorTestCase

@RunWith(classOf[JUnitRunner])
class PduCodecTest extends ValidatorTestCase with MockitoSugar {

  test("should encode NoDataAvailablePdu") {
    val encoder = new PduEncoder()
    val channel = mock[Channel]
    val encoded = encoder.encode(null, channel, PduTest.NoDataAvailablePdu)
    
    assert(encoded.isInstanceOf[ChannelBuffer])
    val buffer = encoded.asInstanceOf[ChannelBuffer]
    buffer.array() should equal(PduTest.NoDataAvailablePduBytes)
  }
  
  test("should decoded NoDataAvailablePduBytes") {
    val decoder = new PduDecoder
    val channelBuffer = new BigEndianHeapChannelBuffer(PduTest.NoDataAvailablePduBytes)
    val channel = mock[Channel]
    
    decoder.decode(null, channel, channelBuffer) match {
      case Right(pdu: ErrorPdu) =>
        pdu.errorCode should equal(ErrorPdu.NoDataAvailable)
      case _ =>
        fail("not an error pdu")
    }
  }
  
}
