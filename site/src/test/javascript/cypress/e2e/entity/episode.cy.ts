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

describe('Episode e2e test', () => {
  const episodePageUrl = '/episode';
  const episodePageUrlPattern = new RegExp('/episode(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const episodeSample = { title: 'feed' };

  let episode;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/episodes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/episodes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/episodes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (episode) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/episodes/${episode.id}`,
      }).then(() => {
        episode = undefined;
      });
    }
  });

  it('Episodes menu should load Episodes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('episode');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Episode').should('exist');
    cy.url().should('match', episodePageUrlPattern);
  });

  describe('Episode page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(episodePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Episode page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/episode/new$'));
        cy.getEntityCreateUpdateHeading('Episode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', episodePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/episodes',
          body: episodeSample,
        }).then(({ body }) => {
          episode = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/episodes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/episodes?page=0&size=20>; rel="last",<http://localhost/api/episodes?page=0&size=20>; rel="first"',
              },
              body: [episode],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(episodePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Episode page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('episode');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', episodePageUrlPattern);
      });

      it('edit button click should load edit Episode page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Episode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', episodePageUrlPattern);
      });

      it.skip('edit button click should load edit Episode page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Episode');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', episodePageUrlPattern);
      });

      it('last delete button click should delete instance of Episode', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('episode').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', episodePageUrlPattern);

        episode = undefined;
      });
    });
  });

  describe('new Episode page', () => {
    beforeEach(() => {
      cy.visit(`${episodePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Episode');
    });

    it('should create an instance of Episode', () => {
      cy.get(`[data-cy="title"]`).type('SSL Coche').should('have.value', 'SSL Coche');

      cy.get(`[data-cy="synopsis"]`).type('synergistic PNG').should('have.value', 'synergistic PNG');

      cy.get(`[data-cy="order"]`).type('60295').should('have.value', '60295');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        episode = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', episodePageUrlPattern);
    });
  });
});
