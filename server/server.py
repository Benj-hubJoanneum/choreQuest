from flask import Flask, jsonify, request, make_response, send_from_directory
import os
import logging
from werkzeug.utils import secure_filename

app = Flask(__name__)

UPLOAD_FOLDER = 'upload'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

logging.basicConfig(level=logging.INFO)

def allowed_file(filename):
    """Check if the file extension is allowed."""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/images/upload', methods=['POST'])
def upload_image():
    """
    Upload an image file and save it to the server. 
    Respond with detailed information about the uploaded file.
    """
    if 'image' not in request.files:
        logging.error("No image part in the request")
        return jsonify({
            'success': False,
            'error': 'No image part in the request',
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

    if file and allowed_file(file.filename):
        try:
            filename = secure_filename(file.filename)
            file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(file_path)

            logging.info(f"File uploaded successfully: {filename}")

            # Construct file URL for response
            file_url = f"{request.host_url}{UPLOAD_FOLDER}/{filename}"
            return jsonify({
                'success': True,
                'message': 'Image uploaded successfully',
                'filename': filename,
                'file_url': file_url,
                'file_path': file_path
            }), 200
        except Exception as e:
            logging.error(f"Error saving file: {str(e)}")
            return jsonify({
                'success': False,
                'error': 'Internal server error',
                'details': str(e)
            }), 500

    logging.error("File type not allowed")
    return jsonify({
        'success': False,
        'error': 'File type not allowed',
        'details': f'Allowed file types: {", ".join(ALLOWED_EXTENSIONS)}'
    }), 400

@app.route('/upload/<filename>', methods=['GET'])
def serve_file(filename):
    """
    Serve uploaded files for testing file access.
    """
    try:
        return send_from_directory(app.config['UPLOAD_FOLDER'], filename)
    except FileNotFoundError:
        logging.error(f"File not found: {filename}")
        return jsonify({'success': False, 'error': 'File not found'}), 404

if __name__ == '__main__':
    app.run(debug=True)
