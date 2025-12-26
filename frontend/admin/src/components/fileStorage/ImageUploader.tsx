import React, { useState, useRef } from 'react';
import axios from 'axios'; 
import { Upload, Loader2, CheckCircle } from 'lucide-react';

const CLOUD_NAME = 'dpasjinez';
const UPLOAD_PRESET = 'theater_unsigned';

interface ImageUploaderProps {
  onUploadSuccess?: (url: string) => void;
  accept?: string;
  maxSizeMB?: number;
}

function ImageUploader({
  onUploadSuccess, 
  accept = "image/*",
  maxSizeMB = 5
}: ImageUploaderProps) {
  const [loading, setLoading] = useState(false);
  const [uploadSuccess, setUploadSuccess] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validate file size
    const fileSizeMB = file.size / (1024 * 1024);
    if (fileSizeMB > maxSizeMB) {
      alert(`File size must be less than ${maxSizeMB}MB`);
      return;
    }

    setLoading(true);
    setUploadSuccess(false);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', UPLOAD_PRESET);

    try {
      const response = await axios.post(
        `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      );

      const secureUrl = response.data.secure_url;
      setUploadSuccess(true);
      onUploadSuccess?.(secureUrl);
      
      // Reset file input
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }

      // Reset success message after 3 seconds
      setTimeout(() => {
        setUploadSuccess(false);
      }, 3000);

    } catch (error) {
      console.error('Error uploading image:', error);
      alert('Failed to upload image. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return(
    <div className="space-y-2">
      <div className="flex items-center gap-3">
        <label 
          htmlFor="image-upload" 
          className="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-md cursor-pointer hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
          style={{ pointerEvents: loading ? 'none' : 'auto', opacity: loading ? 0.5 : 1 }}
        >
          {loading ? (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              Uploading...
            </>
          ) : uploadSuccess ? (
            <>
              <CheckCircle className="h-4 w-4" />
              Uploaded!
            </>
          ) : (
            <>
              <Upload className="h-4 w-4" />
              Choose Image
            </>
          )}
        </label>
        <input 
          ref={fileInputRef}
          id="image-upload"
          type="file" 
          accept={accept} 
          onChange={handleFileChange} 
          disabled={loading}
          className="hidden"
        />
        {!loading && !uploadSuccess && (
          <span className="text-sm text-muted-foreground">
            Max size: {maxSizeMB}MB
          </span>
        )}
      </div>
    </div> 
  );
}

export { ImageUploader };