# Contributing guidelines

We love external contributions! 

Here are some guidelines to streamline the process.


## Coding

1. Fork the repo
2. Create a branch in your fork (prefer - in naming rather than _)
3. Implement your changes in your branch
    - Make sure your branch contains changes limited to the scope of the task
    - Dependency updates should be standalone PRs whenever possible
    - Implement tests if applicable
4. Do a round of manual QA
5. Update `CHANGELOG.md` if applicable
6. Update documentation if applicable
7. Push to your fork's branch, open a PR

Trivial fixes (typo, easy-to-fix compilation error, etc.) don't need to go through this process


## Documentation
* <kbd>docs</kbd> - contains the source for the GitHub page

When updating the documentation, test the generated MkDocs site locally.

### Setup

**Prerequisites**

- Python

```bash
python3 -m pip install --upgrade pip     # install pip
python3 -m pip install mkdocs            # install MkDocs 
python3 -m pip install mkdocs-material   # install material theme
python3 -m pip install mkdocs-git-revision-date-localized-plugin
python3 -m pip install mkdocs-minify-plugin
python3 -m pip install mkdocs-macros-plugin
```


### Run

```bash
mkdocs serve
```

Check the console output for a localhost url, most probably something like:

```
INFO     -  [09:41:08] Serving on http://127.0.0.1:8000/Foso/Ktorfit
```

Open the url in your browser. Changes are automatically deployed by MkDocs while the server is running.

