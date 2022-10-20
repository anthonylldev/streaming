import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Film e2e test', () => {
  const filmPageUrl = '/film';
  const filmPageUrlPattern = new RegExp('/film(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const filmSample = { title: 'Huerta Diverso', url: 'https://mercedes.es' };

  let film;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/films+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/films').as('postEntityRequest');
    cy.intercept('DELETE', '/api/films/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (film) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/films/${film.id}`,
      }).then(() => {
        film = undefined;
      });
    }
  });

  it('Films menu should load Films page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('film');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Film').should('exist');
    cy.url().should('match', filmPageUrlPattern);
  });

  describe('Film page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(filmPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Film page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/film/new$'));
        cy.getEntityCreateUpdateHeading('Film');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/films',
          body: filmSample,
        }).then(({ body }) => {
          film = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/films+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/films?page=0&size=20>; rel="last",<http://localhost/api/films?page=0&size=20>; rel="first"',
              },
              body: [film],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(filmPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Film page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('film');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });

      it('edit button click should load edit Film page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Film');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });

      it.skip('edit button click should load edit Film page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Film');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });

      it('last delete button click should delete instance of Film', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('film').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);

        film = undefined;
      });
    });
  });

  describe('new Film page', () => {
    beforeEach(() => {
      cy.visit(`${filmPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Film');
    });

    it('should create an instance of Film', () => {
      cy.get(`[data-cy="title"]`).type('Decoración leading-edge Gris').should('have.value', 'Decoración leading-edge Gris');

      cy.get(`[data-cy="synopsis"]`).type('Tácticas Gris').should('have.value', 'Tácticas Gris');

      cy.get(`[data-cy="views"]`).type('12319').should('have.value', '12319');

      cy.setFieldImageAsBytesOfEntity('cover', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="reviews"]`).type('21370').should('have.value', '21370');

      cy.get(`[data-cy="gender"]`).select('ROMANCE');

      cy.get(`[data-cy="filmType"]`).select('DOCUMENTARY');

      cy.get(`[data-cy="order"]`).type('52055').should('have.value', '52055');

      cy.get(`[data-cy="url"]`).type('https://ignacio.com').should('have.value', 'https://ignacio.com');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        film = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', filmPageUrlPattern);
    });
  });
});
