import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FileUpload } from 'primeng/fileupload';
import { Observable, Observer } from 'rxjs';

export type FileLoadErrorType = 'not.image' | 'could.not.extract' | 'not.file.upload';

export interface FileLoadError {
  message: string;
  key: FileLoadErrorType;
  params?: any;
}

/**
 * An utility service for data.
 */
@Injectable({
  providedIn: 'root',
})
export class DataUtils {
  /**
   * Method to find the byte size of the string provides
   */
  byteSize(base64String: string): string {
    return this.formatAsBytes(this.size(base64String));
  }

  /**
   * Method to open file
   */
  openFile(data: string, contentType: string | null | undefined): void {
    contentType = contentType ?? '';

    const byteCharacters = atob(data);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], {
      type: contentType,
    });
    const fileURL = window.URL.createObjectURL(blob);
    const win = window.open(fileURL);
    win!.onload = function () {
      URL.revokeObjectURL(fileURL);
    };
  }

  /**
   * Sets the base 64 data & file type of the 1st file on the event (event.target.files[0]) in the passed entity object
   * and returns an observable.
   *
   * @param event the object containing the file (at event.target.files[0])
   * @param editForm the form group where the input field is located
   * @param field the field name to set the file's 'base 64 data' on
   * @param isImage boolean representing if the file represented by the event is an image
   * @returns an observable that loads file to form field and completes if sussessful
   *      or returns error as FileLoadError on failure
   */
  loadFileToForm(event: Event, editForm: FormGroup, field: string, isImage: boolean): Observable<void> {
    return new Observable((observer: Observer<void>) => {
      const eventTarget: HTMLInputElement | null = event.target as HTMLInputElement | null;
      if (eventTarget?.files?.[0]) {
        const file: File = eventTarget.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          const error: FileLoadError = {
            message: `File was expected to be an image but was found to be '${file.type}'`,
            key: 'not.image',
            params: { fileType: file.type },
          };
          observer.error(error);
        } else {
          const fieldContentType: string = field + 'ContentType';
          this.toBase64(file, (base64Data: string) => {
            editForm.patchValue({
              [field]: base64Data,
              [fieldContentType]: file.type,
            });
            observer.next();
            observer.complete();
          });
        }
      } else {
        const error: FileLoadError = {
          message: 'Could not extract file',
          key: 'could.not.extract',
          params: { event },
        };
        observer.error(error);
      }
    });
  }

  loadFile(event: any, editForm: FormGroup, field: string): Observable<void> {
    return new Observable((observer: Observer<void>) => {
      const fieldContentType: string = field + 'ContentType';
      const file = event.files[0];
      if (file) {
        this.toBase64(file, (base64Data: string) => {
          editForm.patchValue({
            [field]: base64Data,
            [fieldContentType]: file.type,
          });
          observer.next();
          observer.complete();
        });
      } else {
        const error: FileLoadError = {
          message: 'Could not extract file',
          key: 'could.not.extract',
          params: { event },
        };
        observer.error(error);
      }
    });
  }

  loadFileInFileUpload(cover: string, coverContentType: string, fileUpload?: FileUpload): Observable<void> {
    return new Observable((observer: Observer<void>) => {
      if (fileUpload) {
        fileUpload.clear();
        if (cover && coverContentType) {
          const file = this.toFile(cover, coverContentType);
          fileUpload.files.push(file);
          observer.next();
          observer.complete();
        } else {
          const error: FileLoadError = {
            message: 'Image not found',
            key: 'not.image',
            params: { fileUpload },
          };
          observer.error(error);
        }
      } else {
        const error: FileLoadError = {
          message: 'File upload not found',
          key: 'not.file.upload',
          params: { fileUpload },
        };
        observer.error(error);
      }
    });
  }

  /**
   * Method to convert the base64 to file
   *
   * TODO set objectUrl to file
   */
  private toFile(base64: string, coverContentType: string): File {
    const byteString = window.atob(base64);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const int8Array = new Uint8Array(arrayBuffer);

    for (let i = 0; i < byteString.length; i++) {
      int8Array[i] = byteString.charCodeAt(i);
    }

    const blob = new Blob([int8Array], { type: coverContentType });

    // URL.createObjectURL(blob)

    const file = new File([blob], coverContentType, {type: coverContentType });

    return file;
  }

  /**
   * Method to convert the file to base64
   */
  private toBase64(file: File, callback: (base64Data: string) => void): void {
    const fileReader: FileReader = new FileReader();
    fileReader.onload = (e: ProgressEvent<FileReader>) => {
      if (typeof e.target?.result === 'string') {
        const base64Data: string = e.target.result.substring(e.target.result.indexOf('base64,') + 'base64,'.length);
        callback(base64Data);
      }
    };
    fileReader.readAsDataURL(file);
  }

  private endsWith(suffix: string, str: string): boolean {
    return str.includes(suffix, str.length - suffix.length);
  }

  private paddingSize(value: string): number {
    if (this.endsWith('==', value)) {
      return 2;
    }
    if (this.endsWith('=', value)) {
      return 1;
    }
    return 0;
  }

  private size(value: string): number {
    return (value.length / 4) * 3 - this.paddingSize(value);
  }

  private formatAsBytes(size: number): string {
    return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ') + ' bytes'; // NOSONAR
  }
}
