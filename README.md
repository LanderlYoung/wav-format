An implementation of parse wave file in java language.
Simple as it is,  use the following code is enough to parse an wav file header: 

```java
in = new FileInputStream(new File("/home/young/Desktop/sample.wav"));
int amount = in.read(wavHeader);
WavHeaderInfo headerInfo = WavHeaderInfo.parseHeader(wavHeader, 0, amount);
System.out.println(headerInfo);
System.out.println("wav sample rate:" + headerInfo.mFmtChunk.SampleRate);
System.out.println("wav bits per sample:" + headerInfo.mFmtChunk.BitsPerSample);
System.out.println(headerInfo.getDataSize());
System.out.println(headerInfo.getFileSize());
System.out.println(headerInfo.getHeaderSize());
```

the output is :
```bash
{ ChunkID:RIFF, ChunkSize:2646070, Format:WAVE }
{ ChunkID:fmt , ChunkSize:16, AudioFormat:1, NumChannels:1, SampleRate:44100, ByteRate:88200, BlockAlign:2, BitsPerSample:16 }
{ ChunkID:LIST, ChunkSize:26, (unknown chunk) ... }
{ ChunkID:data, ChunkSize:2646000, Data: not read }

wav sample rate:44100
wav bits per sample:16
2646000
2646078
78
```
Seeing this, you may know where to get the information you need! Take a shot!

Wanna get more information from `WavHeaderInfo` ? 
Why not dive into [source file][1] and get the source coude as well as javadoc! 
For information about wav header format,  please refer to [here][2].

Bug reporting and suggestions are welcome.

[1]:com/young/format/WavHeaderInfo.java
[2]:https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
