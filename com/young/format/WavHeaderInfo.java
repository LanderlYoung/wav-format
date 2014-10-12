package com.young.format;

import com.young.util.Memory;
import com.young.util.Sizeof;

import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author: landerlyoung
 * Date:   2014-10-11
 * Time:   14:38
 * Life with passion. Code with creativity!
 * wav file format parser.
 * wav header information: <a href="https://ccrma.stanford.edu/courses/422/projects/WaveFormat/" >here</a>
 * So this class support only too sub-chunk information: "fmt " and "data"
 */
public class WavHeaderInfo {
    public RIFF_Chunk mRiffChunk;
    public FMT_Chunk mFmtChunk;
    public DATA_Chunk mDataChunk;
    public ArrayList<Unknown_Chunk> mUnknownChunkList;

    private WavHeaderInfo() {
        mUnknownChunkList = new ArrayList<Unknown_Chunk>();
    }

    public static WavHeaderInfo create(byte[] data) {
        return create(data, 0, data.length);
    }

    /**
     * @param data
     * @param offset
     * @param len
     * @return
     */
    public static WavHeaderInfo create(byte[] data, int offset, int len) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mRiffChunk != null) {
            sb.append(mRiffChunk.toString());
            sb.append("\n");
        }
        if (mFmtChunk != null) {
            sb.append(mFmtChunk.toString());
            sb.append("\n");
        }
        for (Unknown_Chunk u : mUnknownChunkList) {
            sb.append(u.toString());
            sb.append("\n");

        }
        if (mDataChunk != null) {
            sb.append(mDataChunk);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * For why plus 8, refer to {@link RIFF_Chunk#ChunkSize}.
     *
     * @return total file size
     */
    public int getFileSize() {
        return mRiffChunk.ChunkSize + 8;
    }

    public int getHeaderSize() {
        int unknown_chunk_size = 0;
        for (Unknown_Chunk uc : mUnknownChunkList) {
            unknown_chunk_size += uc.getChunkSize();
        }
        return /*riff chunk size*/ 12 +
                mFmtChunk.getChunkSize() +
                unknown_chunk_size +
                /*data chunk head size*/ 8;
    }

    public int getDataSize() {
        return getFileSize() - getHeaderSize();
    }

    /**
     * Parse a wav file,
     *
     * @param data   wav file
     * @param offset offset of array
     * @param len    length of data
     * @return wave file info, or null, if this is not a valid wav file.
     */
    public static WavHeaderInfo parseHeader(byte[] data, int offset, int len) {
        try {
            WavHeaderInfo instance = new WavHeaderInfo();
            if (BaseChunk.isChunkIDEuqla(data, offset, RIFF_Chunk.CONST_CHUNK_ID, 0)) {
                instance.mRiffChunk = RIFF_Chunk.create(data, offset, len);
                offset += 12;
            } else {
                throw new FormatNotSupportedException("no RIFF chunk");
            }

            while (true) {
                if (BaseChunk.isChunkIDEuqla(data, offset,
                        FMT_Chunk.CONST_CHUNK_ID, 0)) {
                    instance.mFmtChunk = FMT_Chunk.create(data, offset, len - offset);
                    offset += instance.mFmtChunk.getChunkSize();
                } else if (BaseChunk.isChunkIDEuqla(
                        data, offset, DATA_Chunk.CONST_CHUNK_ID, 0)) {
                    instance.mDataChunk = DATA_Chunk.create(data, offset, len - offset);
                    break;
                } else {
                    Unknown_Chunk un = Unknown_Chunk.create(data, offset, len - offset);
                    offset += un.getChunkSize();
                    instance.mUnknownChunkList.add(un);
                }
            }

            if (instance.mFmtChunk == null ) {
                throw new FormatNotSupportedException("no FMT chunk");
            }
            if(instance.mDataChunk == null) {
                throw new FormatNotSupportedException("no Data chunk");
            }
            return instance;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        } catch (FormatNotSupportedException fe) {
            return null;
        }
    }

    private static class BaseChunk {
        /**
         * This is the sub-chunk id for RIFF.
         * A string of 4 char.
         */
        public byte[] ChunkID = new byte[4];

        /**
         * This is the size of the rest of the chunk
         * following this number.  This is the size of the
         * entire file in bytes minus 8 bytes for the
         * two fields not included in this count:
         * ChunkID and ChunkSize.
         */
        public int ChunkSize;

        protected BaseChunk() {

        }

        public BaseChunk doCreate(byte[] data, int offset, int len) {
            if (offset < 0 || data.length < offset + len) {
                throw new IllegalArgumentException("arrary length:" + data.length +
                        " offset:" + offset + " len:" + len);
            }
            BaseChunk chunk = new BaseChunk();
            System.arraycopy(data, offset, ChunkID, 0, ChunkID.length);
            offset += 4;
            ChunkSize = Memory.peekInt(data, offset, ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        /**
         * @return total size of this chunk. <em>NOT {@link #ChunkSize}!</em>
         */
        public int getChunkSize() {
            return 8 + ChunkSize;
        }

        public static boolean isChunkIDEuqla(byte[] one, int oneOffset,
                                             byte[] another, int anotherOffset) {
            if (one != null && another != null
                    && one.length >= 4 + oneOffset
                    && another.length >= 4 + anotherOffset) {
                for (int i = 0; i < 4; i++) {
                    if (one[i + oneOffset] != another[i + anotherOffset]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "{ ChunkID:" + new String(ChunkID) + ", ChunkSize:" + ChunkSize;
        }
    }

    public static class Unknown_Chunk extends BaseChunk {
        private Unknown_Chunk() {

        }

        public static Unknown_Chunk create(byte[] data, int offset, int len) {
            Unknown_Chunk u = new Unknown_Chunk();
            u.doCreate(data, offset, len);
            return u;
        }

        @Override
        public String toString() {
            return super.toString() + ", (unknown chunk) ... }";
        }
    }

    public static class RIFF_Chunk extends BaseChunk {
        /**
         * ChunkID ie: string of "RIFF"
         */
        public static final byte[] CONST_CHUNK_ID = {
                'R', 'I', 'F', 'F',
        };

        public byte[] Format = new byte[4];

        public static final byte[] CONST_FORMAT = {
                'W', 'A', 'V', 'E',
        };

        private RIFF_Chunk() {

        }

        @Override
        public BaseChunk doCreate(byte[] data, int offset, int len) {
            super.doCreate(data, offset, len);
            offset += 8;
            System.arraycopy(data, offset, Format, 0, 4);
            return this;
        }

        @Override
        public String toString() {
            return super.toString() + ", Format:" + new String(Format) + " }";
        }

        public static RIFF_Chunk create(byte[] data, int offset, int len) {
            RIFF_Chunk r = new RIFF_Chunk();
            r.doCreate(data, offset, len);
            return r;
        }
    }

    public static class FMT_Chunk extends BaseChunk {
        public static final byte[] CONST_CHUNK_ID = {
                'f', 'm', 't', ' '//a space
        };

        /**
         * 16 for PCM.  This is the size of the
         * rest of the Subchunk which follows this number.
         */
        //public int ChunkSize; //existed in super class

        /**
         * AudioFormat PCM = 1 (i.e. Linear quantization)
         * <br/>
         * Values other than 1 indicate some
         * form of compression.
         */
        public short AudioFormat;

        /**
         * NumChannels  Mono = 1, Stereo = 2, etc.
         *
         * @see #NumChannels_Mono
         * @see #NumChannels_Stereo
         */
        public short NumChannels;

        /**
         * SampleRate 8000, 44100, etc.
         */
        public int SampleRate;

        /**
         * ByteRate == SampleRate * NumChannels * BitsPerSample/8
         */
        public int ByteRate;

        /**
         * BlockAlign == NumChannels * BitsPerSample/8
         * <br/>
         * The number of bytes for one sample including
         * all channels. I wonder what happens when
         * this number isn't an integer?
         */
        public short BlockAlign;

        /**
         * BitsPerSample 8 bits = 8, 16 bits = 16, etc.
         */
        public short BitsPerSample;

        private FMT_Chunk() {

        }

        @Override
        public BaseChunk doCreate(byte[] data, int offset, int len) {
            super.doCreate(data, offset, len);
            offset += 8;
            AudioFormat = Memory.peekShort(data, offset, ByteOrder.LITTLE_ENDIAN);
            offset += Sizeof.SHORT;
            NumChannels = Memory.peekShort(data, offset, ByteOrder.LITTLE_ENDIAN);
            offset += Sizeof.SHORT;
            SampleRate = Memory.peekInt(data, offset, ByteOrder.LITTLE_ENDIAN);
            offset += Sizeof.INT;
            ByteRate = Memory.peekInt(data, offset, ByteOrder.LITTLE_ENDIAN);
            offset += Sizeof.INT;
            BlockAlign = Memory.peekShort(data, offset, ByteOrder.LITTLE_ENDIAN);
            offset += Sizeof.SHORT;
            BitsPerSample = Memory.peekShort(data, offset, ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        public static FMT_Chunk create(byte[] data, int offset, int len) {
            FMT_Chunk fmt = new FMT_Chunk();
            fmt.doCreate(data, offset, len);
            return fmt;
        }

        @Override
        public String toString() {
            return super.toString() +
                    ", AudioFormat:" + AudioFormat +
                    ", NumChannels:" + NumChannels +
                    ", SampleRate:" + SampleRate +
                    ", ByteRate:" + ByteRate +
                    ", BlockAlign:" + BlockAlign +
                    ", BitsPerSample:" + BitsPerSample +
                    " }";
        }

        //============constance of field==================
        /**
         * constance of {@link #AudioFormat}
         */
        public static final short AudioFormat_PCM = 1;

        /**
         * constance of {@link #NumChannels}
         *
         * @see #NumChannels
         * @see #NumChannels_Stereo
         */
        public static final short NumChannels_Mono = 1;
        /**
         * constance of {@link #NumChannels}
         *
         * @see #NumChannels
         * @see #NumChannels_Mono
         */
        public static final short NumChannels_Stereo = 2;
        //=================================================
    }

    public static class DATA_Chunk extends BaseChunk {
        public static final byte[] CONST_CHUNK_ID = {
                'd', 'a', 't', 'a',
        };

        /**
         * Subchunk2Size == NumSamples * NumChannels * BitsPerSample/8
         * <br/>
         * This is the number of bytes in the data.
         * You can also think of this as the size
         * of the read of the subchunk following this
         * number.
         */

        /**
         * Won't fill this file.<em>ALWAYS NULL!</em>
         */
        public byte[] Data;

        private DATA_Chunk() {

        }

        @Override
        public BaseChunk doCreate(byte[] data, int offset, int len) {
            return super.doCreate(data, offset, len);
        }

        public static DATA_Chunk create(byte[] data, int offset, int len) {
            DATA_Chunk data_chunk = new DATA_Chunk();
            data_chunk.doCreate(data, offset, len);
            return data_chunk;
        }

        @Override
        public String toString() {
            return super.toString() +
                    ", Data: not read }";
        }
    }
}
