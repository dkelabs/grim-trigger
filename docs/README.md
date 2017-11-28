# DDrone Documentation

Docs can be found at https://grim-trigger.dkelabs.com with the login provided separately.

## Setup Build Environment

``` bash
# install Sphinx
sudo pip install Sphinx

# install the read the docs theme
sudo pip install sphinx_rtd_theme

# Needed to build Sphinx latexpdf
sudo apt -y install texlive-formats-extra
```

## Building The Documentation

Just run the `./build_docs.sh` script.

It will build `html` and `latexpdf` and copy the `grim-trigger-docs.pdf` to this directory.

To deploy the `html` files copy the contents of the entire `build/html` directory into the root directory of the website it is to be hosted on. Alternatively you can open the local `build/html/index.html` file in your local browser.


## Manually Building The Documentation

List all of the target build environments by typing `make` in the source directory. Possible <target> build environments include:

* `html`
* `latex`
* `latexpdf`
* `epub`
* `json`

``` bash
make <target>
```
