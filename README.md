An implementation of parse wave file in java language.
Simple as it is,  use the following code is enough to parse an wav file header: 

```java
in = new FileInputStream(new File("sample.wav"));
int amount = in.read(wavHeader);
WavHeaderInfo headerInfo = WavHeaderInfo.parseHeader(wavHeader, 0, amount);

System.out.println(headerInfo);
System.out.println("Sample Rate:" + headerInfo.mFmtChunk.SampleRate);
System.out.println("Bits Per Sample:" + headerInfo.mFmtChunk.BitsPerSample);
System.out.println("Bit Rate:" + headerInfo.getBitRate());
System.out.println("Duration:" + headerInfo.getDuration());
System.out.println("Data Size:" + headerInfo.getDataSize());
System.out.println("File Size:" + headerInfo.getFileSize());
System.out.println("Header Size:" + headerInfo.getHeaderSize());
```

the output is :
```
{ ChunkID:RIFF, ChunkSize:1764172, Format:WAVE }
{ ChunkID:fmt , ChunkSize:16, AudioFormat:1, NumChannels:2, SampleRate:44100, ByteRate:176400, BlockAlign:4, BitsPerSample:16 }
{ ChunkID:LIST, ChunkSize:128, (unknown chunk) ... }
{ ChunkID:data, ChunkSize:1764000, Data: not read }

Sample Rate:44100
Bits Per Sample:16
Bit Rate:1411200
Duration:10000
Data Size:1764000
File Size:1764180
Header Size:180
```
Seeing this, you may know where to get the information you need! Take a shot!

Wanna get more information from `WavHeaderInfo` ? 
Why not dive into [source file][1] and get the source code as well as javadoc!
For information about wav header format,  please refer to [here][2].

Bug reporting and suggestions are welcome.

[1]:com/young/format/WavHeaderInfo.java
[2]:https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
