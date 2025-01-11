from flask import Flask, jsonify, request, send_from_directory
import os
import logging
from werkzeug.utils import secure_filename
import shutil

app = Flask(__name__)

UPLOAD_FOLDER = 'upload'
FINAL_FOLDER = 'images'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(FINAL_FOLDER, exist_ok=True)  # Ensure the 'images' folder exists

logging.basicConfig(level=logging.INFO)

def allowed_file(filename):
    """Check if the file extension is allowed."""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def process_image(file_path):
    """
    A placeholder method to perform some action on the uploaded image.
    Replace this method's implementation with actual processing logic.
    """
    logging.info(f"Processing image: {file_path}")
    # Add your image processing logic here (e.g., resizing, watermarking)

@app.route('/images/upload', methods=['POST'])
def upload_file():
    """
    Upload a file (any type), process it, and move it to the 'images' folder.
    """
    if 'image' not in request.files:
        logging.error("No file part in the request")
        return jsonify({
            'success': False,
            'error': 'No file part in the request',
            'details': 'Expected a file part named "image".'
        }), 400

    file = request.files['image']
    if file.filename == '':
        logging.error("No file selected for upload")
        return jsonify({
            'success': False,
            'error': 'No file selected for upload',
            'details': 'The "image" file part is empty.'
        }), 400

    try:
        # Use custom name if provided; otherwise, use the original filename
        custom_name = request.form.get('custom_name')
        original_filename = secure_filename(custom_name if custom_name else file.filename)
        
        # Ensure the file ends with .jpg
        if not original_filename.lower().endswith('.jpg'):
            filename = f"{os.path.splitext(original_filename)[0]}.jpg"
        else:
            filename = original_filename

        temp_file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        
        # Save the uploaded file temporarily
        file.save(temp_file_path)
        logging.info(f"File uploaded successfully: {filename}")

        # Move the file to the 'images' folder
        final_file_path = os.path.join(FINAL_FOLDER, filename)
        shutil.move(temp_file_path, final_file_path)
        logging.info(f"File moved to final folder: {final_file_path}")

        # Construct file URL for response
        file_url = f"{request.host_url}{FINAL_FOLDER}/{filename}"
        return jsonify({
            'success': True,
            'message': 'File uploaded and moved successfully',
            'filename': filename,
            'file_url': file_url,
            'final_file_path': final_file_path
        }), 200

    except Exception as e:
        logging.error(f"Error handling file: {str(e)}")
        return jsonify({
            'success': False,
            'error': 'Internal server error',
            'details': str(e)
        }), 500


@app.route('/images/<filename>', methods=['GET'])
def serve_image(filename):
    """
    Serve files from the 'images' folder.
    """
    try:
        logging.info(f"Serving image: {filename}")
        return send_from_directory(FINAL_FOLDER, filename)
    except FileNotFoundError:
        logging.error(f"File not found: {filename}")
        return jsonify({'success': False, 'error': 'File not found'}), 404

if __name__ == '__main__':
    app.run(debug=True)
